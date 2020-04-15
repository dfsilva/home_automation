package br.com.diegosilva.automation.actors;

import akka.actor.Cancellable;
import akka.actor.typed.Behavior;
import akka.actor.typed.PreRestart;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.time.Duration;

public class Receptor extends AbstractBehavior<Receptor.Command> {

    private SerialPort serialPort;
    private Cancellable cancellable;

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
        getContext().getLog().debug("Iniciando classe main");
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
                    String valorRecebido = new String(buffer);

                    getContext().getLog().debug("Valor recebido {}", valorRecebido);
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

    public interface Response {
    }


    public interface Command {
    }

    public static class Start implements Command {
    }
}
