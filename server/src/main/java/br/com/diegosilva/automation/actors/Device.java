package br.com.diegosilva.automation.actors;

import akka.actor.Cancellable;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import br.com.diegosilva.automation.CborSerializable;
import br.com.diegosilva.automation.dto.IOTMessage;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.Duration;

public class Device extends AbstractBehavior<Device.Command> {

    private Cancellable cancellable;
    private final String deviceId;


    public static final EntityTypeKey<Command> TypeKey =
            EntityTypeKey.create(Device.Command.class, "Device");

    public static void initSharding(ActorSystem<?> system) {
        ClusterSharding.get(system).init(Entity.of(TypeKey, entityContext ->
                Device.create(entityContext.getEntityId())
        ));
    }

    public static Behavior<Command> create(String deviceid) {
        return Behaviors.setup(context -> new Device(context, deviceid));
    }

    private Device(ActorContext<Command> context, String deviceId) {
        super(context);
        this.deviceId = deviceId;
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(Process.class, (msg) -> {
                    getContext().getLog().info("Processando mensagem IOT {}", msg.message);
                    return this;
                })
                .onMessage(Send.class, (msg) -> {
                    getContext().getLog().info("Enviando mensagem para  {}", msg.message.id);
                    msg.replyTo.tell(new SendResponse("Mensagem enviada"));
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

    public interface Response extends CborSerializable {
    }

    public static class Process implements Command {
        final IOTMessage message;

        @JsonCreator
        public Process(IOTMessage message) {
            this.message = message;
        }
    }


    public static class Send implements Command {
        final IOTMessage message;
        public final ActorRef<Response> replyTo;

        @JsonCreator
        public Send(IOTMessage message, ActorRef<Response> replyTo) {
            this.message = message;
            this.replyTo = replyTo;
        }
    }

    public static class SendResponse implements Response {
        public final String message;

        @JsonCreator
        public SendResponse(String message) {
            this.message = message;
        }
    }
}
