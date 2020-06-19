package br.com.diegosilva.home.actors

import akka.actor.typed.pubsub.Topic
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import br.com.diegosilva.home.CborSerializable
import br.com.diegosilva.home.data.Lecture
import org.slf4j.{Logger, LoggerFactory}

object WsUserActor {

  sealed trait Command extends CborSerializable

  case class Connect(actorRef: ActorRef[Command]) extends Command

  final case class Connected(userName: String, message: String) extends Command

  case object Disconnected extends Command

  case class Fail(ex: Throwable) extends Command

  final case class Notify(message: Lecture) extends Command

  final case class Register(uids: List[String]) extends Command

}

class WsUserActor(val userId: String) {

  private var actorRef: ActorRef[WsUserActor.Command] = null
  var topics: Map[String, ActorRef[Topic.Command[WsUserActor.Notify]]] = Map()
  private val log: Logger = LoggerFactory.getLogger(WsUserActor.getClass)

  def create(): Behavior[WsUserActor.Command] = Behaviors.setup { context =>

    Behaviors.receiveMessage[WsUserActor.Command] {
      case WsUserActor.Register(uids) =>
        uids.foreach { uid =>
          log.error("Conectando ao topico {}", uid)
          val topicName = s"notify_$uid"
          if (!topics.contains(topicName)) {
            val topic: ActorRef[Topic.Command[WsUserActor.Notify]] = context.spawn(Topic[WsUserActor.Notify](topicName), topicName)
            topic ! Topic.Subscribe(context.self)
            topics = topics + (topicName -> topic)
          }
        }
        Behaviors.same
      case WsUserActor.Notify(message) =>
        actorRef ! WsUserActor.Notify(message)
        Behaviors.same
      case WsUserActor.Connect(actorRef) =>
        this.actorRef = actorRef
        this.actorRef ! WsUserActor.Connected(message = "conectado", userName = userId)
        Behaviors.same
      case WsUserActor.Disconnected => {
        topics.values.foreach { topic =>
          topic ! Topic.unsubscribe(context.self)
        }
        this.topics = topics removedAll topics.keys
        Behaviors.stopped
      }
    }
  }
}
