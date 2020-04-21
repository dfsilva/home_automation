package br.com.diegosilva.automation.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.pubsub.Topic;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import br.com.diegosilva.automation.CborSerializable;


public class UserActor extends AbstractBehavior<UserActor.UserCommand> {


    private final String uid;
    private ActorRef<Topic.Command<UserCommand>> topic;

    public static final EntityTypeKey<UserCommand> TypeKey =
            EntityTypeKey.create(UserCommand.class, "User");

    public static void initSharding(ActorSystem<?> system) {
        ClusterSharding.get(system).init(Entity.of(TypeKey, entityContext ->
                UserActor.create(entityContext.getEntityId())
        ));
    }

    public static Behavior<UserCommand> create(String uid) {
        return Behaviors.setup(context -> new UserActor(context, uid));
    }

    private UserActor(ActorContext<UserCommand> context, String uid) {
        super(context);
        this.uid = uid;
        topic = context.spawn(Topic.create(UserCommand.class, "user_" + uid), "User" + uid);
        topic.tell(Topic.subscribe(getContext().getSelf()));
    }

    @Override
    public Receive<UserCommand> createReceive() {
        return null;
    }

    public interface UserCommand extends CborSerializable {
    }


}
