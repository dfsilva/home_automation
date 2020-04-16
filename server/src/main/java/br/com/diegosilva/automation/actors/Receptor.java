package br.com.diegosilva.automation.actors;

import akka.actor.Cancellable;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PreRestart;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import br.com.diegosilva.automation.CborSerializable;
import br.com.diegosilva.automation.dto.IOTMessage;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class Receptor extends AbstractBehavior<Receptor.Command> {

    private SerialPort serialPort;
    private Cancellable cancellable;
    private Map<String, ActorRef<Device.Command>> devices = new HashMap<>();

    public static Behavior<Command> create() {
        return Behaviors.setup(context -> new Receptor(context));
    }

    private Receptor(ActorContext<Command> context) {
        super(context);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(Start.class, (msg) -> {
                    getContext().getLog().debug("Iniciando escuta");
                    startReceive();
                    return this;
                })
                .onSignal(
                        PreRestart.class,
                        signal -> {
                            getContext().getLog().debug("Reiniciando de recebimento de leituras");
                            retry(Duration.ofSeconds(5), new Receptor.Start());
                            return this;
                        })
                .build();
    }

    public void startReceive() {
        serialPort = new SerialPort(getContext().getSystem().settings().config().getString("serial.port"));
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_57600,
                    SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS
                    + SerialPort.MASK_DSR;
            serialPort.setEventsMask(mask);
            serialPort.addEventListener(event -> {
                try {
                    byte buffer[] = serialPort.readBytes(event.getEventValue());
                    String msg = new String(buffer);
                    getContext().getLog().debug("Valor recebido {}", msg);
                    IOTMessage message = IOTMessage.decode(msg);
                    if (message != null)
                        getDevice(message.id).tell(new Device.Process(message));
                } catch (SerialPortException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SerialPortException ex) {
            getContext().getLog().error("Erro ao receber leitura", ex);
            throw new RuntimeException(ex);
        }
    }

    private void retry(Duration duration, Command cmd) {
        if (this.cancellable != null) {
            this.cancellable.cancel();
        }
        this.cancellable = getContext().scheduleOnce(duration, getContext().getSelf(), cmd);
    }

    ActorRef<Device.Command> getDevice(String id) {
        if (!devices.containsKey(id)) {
            devices.put(id, getContext().spawn(Behaviors.supervise(Device.create())
                    .onFailure(SupervisorStrategy.restartWithBackoff(Duration.ofSeconds(1), Duration.ofSeconds(5), 0.5)), "device_" + id));
        }
        return devices.get(id);
    }


    public interface Command extends CborSerializable {
    }

    public static class Start implements Command {
    }
}
