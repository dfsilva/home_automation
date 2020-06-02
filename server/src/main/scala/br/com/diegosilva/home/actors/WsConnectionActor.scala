package br.com.diegosilva.home.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef}
import br.com.diegosilva.home.CborSerializable
import br.com.diegosilva.home.data.IOTMessage

object WsConnectionActor {

  sealed trait Command extends CborSerializable

  case class Connect(actorRef: ActorRef[Command]) extends Command

  final case class Connected(userName: String, message: String) extends Command

  case object Disconnected extends Command

  case class Fail(ex: Throwable) extends Command

  final case class Notify(message: IOTMessage) extends Command

  final case class Register(uids: List[String]) extends Command

}

class WsConnectionActor(val userName: String) {

  private var actorRef: ActorRef[WsConnectionActor.Command] = null

  def create(): Behavior[WsConnectionActor.Command] = Behaviors.setup { context =>
    val sharding = ClusterSharding(context.system)
    var devices: List[EntityRef[DeviceActor.Command]] = List()

    Behaviors.receiveMessage[WsConnectionActor.Command] {
      case WsConnectionActor.Register(uids) =>
        uids.foreach { uid =>
          val entity = sharding.entityRefFor(DeviceActor.EntityKey, uid)
          devices = devices :+ entity
          entity ! DeviceActor.Register(context.self)
        }
        Behaviors.same
      case WsConnectionActor.Notify(message) =>
        actorRef ! WsConnectionActor.Notify(message)
        Behaviors.same
      case WsConnectionActor.Connect(actorRef) =>
        this.actorRef = actorRef
        this.actorRef ! WsConnectionActor.Connected(message = "conectado", userName = userName)
        Behaviors.same
      case WsConnectionActor.Disconnected => {
        devices foreach { entity =>
          entity ! DeviceActor.UnRegister(context.self)
        }
        Behaviors.stopped
      }
    }
  }
}
