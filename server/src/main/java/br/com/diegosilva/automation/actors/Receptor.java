package br.com.diegosilva.automation.actors;

import akka.actor.Cancellable;
import akka.actor.typed.Behavior;
import akka.actor.typed.PreRestart;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import br.com.diegosilva.automation.CborSerializable;
import br.com.diegosilva.automation.dto.IOTMessage;
import br.com.diegosilva.automation.utils.SerialPortFactory;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.time.Duration;

public class Receptor extends AbstractBehavior<Receptor.Command> {

    private SerialPort serialPort;
    private Cancellable cancellable;

    public static Behavior<Command> create() {
        return Behaviors.setup(context -> new Receptor(context));
    }

    private Receptor(ActorContext<Command> context) throws SerialPortException {
        super(context);
        this.serialPort = SerialPortFactory.get(context.getSystem().settings().config().getString("serial.port"));
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
                            this.serialPort = SerialPortFactory.get(getContext().getSystem().settings().config().getString("serial.port"));
                            retry(Duration.ofSeconds(5), new Receptor.Start());
                            return this;
                        })
                .build();
    }

    public void startReceive() {
        try {
            StringBuilder message = new StringBuilder();
            serialPort.addEventListener(event -> {
                if (event.isRXCHAR() && event.getEventValue() > 0) {
                    try {
                        byte buffer[] = serialPort.readBytes();
                        for (byte b : buffer) {
                            if ((b == '\n') && message.length() > 0) {
                                String toProcess = message.toString().replaceAll("[^a-zA-Z0-9:,;._]", "");;
                                getContext().getLog().info("Valor recebido {}", toProcess);
                                message.setLength(0);
                                IOTMessage iotMessage = IOTMessage.decode(toProcess);
                                if (iotMessage != null)
                                    getDevice(iotMessage.id).tell(new Device.Process(iotMessage));
                            } else {
                                message.append((char) b);
                            }
                        }
                    } catch (SerialPortException ex) {
                        System.out.println(ex);
                        System.out.println("serialEvent");
                    }
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

    EntityRef<Device.Command> getDevice(String id) {
        ClusterSharding sharding = ClusterSharding.get(getContext().getSystem());
        return sharding.entityRefFor(Device.TypeKey, id);
    }

    public interface Command extends CborSerializable {
    }

    public static class Start implements Command {
    }
}
