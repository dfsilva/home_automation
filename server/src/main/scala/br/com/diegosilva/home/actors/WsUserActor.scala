package br.com.diegosilva.home.actors

import akka.actor.typed.pubsub.Topic
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import br.com.diegosilva.home.CborSerializable
import br.com.diegosilva.home.actors.WsUserActor.Register
import br.com.diegosilva.home.data.Lecture
import br.com.diegosilva.home.database.DatabasePool
import br.com.diegosilva.home.repositories.{Device, DeviceRepo}
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Success

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
  private var topics: Map[String, ActorRef[Topic.Command[WsUserActor.Notify]]] = Map()
  private var devices: Seq[Device] = Seq()
  private val log: Logger = LoggerFactory.getLogger(WsUserActor.getClass)


  def create(): Behavior[WsUserActor.Command] = Behaviors.setup { context =>

    val db = DatabasePool(context.system).database
    implicit val executionContext = context.system.executionContext

    Behaviors.receiveMessage[WsUserActor.Command] {
      case WsUserActor.Register(uids) =>
        uids.foreach { uid =>
          log.error("Conectando ao topico {}", uid)
          val topicName = s"notify_$uid"
          subscribe(topicName, context)
        }
        Behaviors.same
      case WsUserActor.Notify(message) =>
        actorRef ! WsUserActor.Notify(message)
        Behaviors.same
      case WsUserActor.Connect(actorRef) =>
        this.actorRef = actorRef
        this.actorRef ! WsUserActor.Connected(message = "conectado", userName = userId)
        val self = context.self
        db.run(DeviceRepo.devicesByUser(userId)).andThen({
          case Success(devs) => {
            devices = devs.toSeq
            self ! Register(devices.map(_.address).toList)

//            devices.foreach(device => {
//              val topicName = s"notify_${device.address}"
//              subscribe(topicName, context)
//            })
          }
        })

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

  def subscribe(topicName: String, context: ActorContext[WsUserActor.Command]): Unit = {
    if (!topics.contains(topicName)) {
      val topic: ActorRef[Topic.Command[WsUserActor.Notify]] = context.spawn(Topic[WsUserActor.Notify](topicName), topicName)
      topic ! Topic.Subscribe(context.self)
      topics = topics + (topicName -> topic)
    }
  }
}
