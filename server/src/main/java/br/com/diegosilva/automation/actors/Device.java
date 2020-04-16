package br.com.diegosilva.automation.actors;

import akka.actor.Cancellable;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import br.com.diegosilva.automation.CborSerializable;
import br.com.diegosilva.automation.dto.IOTMessage;

import java.time.Duration;

public class Device extends AbstractBehavior<Device.Command> {

    private Cancellable cancellable;

    public static Behavior<Command> create() {
        return Behaviors.setup(context -> new Device(context));
    }

    private Device(ActorContext<Command> context) {
        super(context);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(Process.class, (msg) -> {
                    getContext().getLog().debug("Processando mensagem IOT {}", msg.message);
                    return this;
                })
                .build();
    }


    private void retry(Duration duration, Command cmd) {
        if (this.cancellable != null) {
            this.cancellable.cancel();
        }
        this.cancellable = getContext().scheduleOnce(duration, getContext().getSelf(), cmd);
    }


    public interface Command extends CborSerializable {
    }

    public static class Process implements Command {
        final IOTMessage message;

        public Process(IOTMessage message) {
            this.message = message;
        }
    }
}
