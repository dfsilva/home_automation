package br.com.diegosilva.automation.actors;

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
import com.fasterxml.jackson.annotation.JsonCreator;


public class UserActor extends AbstractBehavior<UserActor.Command> {


    private final String uid;

    public static Behavior<Command> create(String uid) {
        return Behaviors.setup(context -> new UserActor(context, uid));
    }

    private UserActor(ActorContext<Command> context, String uid) {
        super(context);
        this.uid = uid;
    }

    @Override
    public Receive<Command> createReceive() {
        return null;
    }

    public interface Command extends CborSerializable {
    }

    public static class Conectar implements Command {
        final ActorRef<Object> actorRef;
        public Conectar(ActorRef<Object> actorRef) {
            this.actorRef = actorRef;
        }
    }

    public static class Desconectado implements Command {
    }

    public static class Falhou implements Command {
        private final Throwable ex;

        public Falhou(Throwable ex) {
            this.ex = ex;
        }
    }


    public static class Soma implements  Command{
        final Integer valor1;
        final Integer valor2;

        public Soma(Integer valor1, Integer valor2) {
            this.valor1 = valor1;
            this.valor2 = valor2;
        }
    }

    public interface Response extends CborSerializable {
    }

    public static class Resultado implements Response {
        public final String message;

        @JsonCreator
        public Resultado(String message) {
            this.message = message;
        }
    }

}
