package br.com.diegosilva.home.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef}
import br.com.diegosilva.home.CborSerializable
import br.com.diegosilva.home.dto.IOTMessage

object WsConnection {

  sealed trait Command extends CborSerializable

  case class Connect(actorRef: ActorRef[Command]) extends Command

  final case class Connected(userName: String, message: String) extends Command

  case object Disconnected extends Command

  case class Fail(ex: Throwable) extends Command

  final case class Notify(message: IOTMessage) extends Command

  final case class Register(uids: List[String]) extends Command

}

class WsConnection(val userName: String) {

  private var actorRef: ActorRef[WsConnection.Command] = null

  def create(): Behavior[WsConnection.Command] = Behaviors.setup { context =>
    val sharding = ClusterSharding(context.system)
    var devices: List[EntityRef[Device.Command]] = List()

    Behaviors.receiveMessage[WsConnection.Command] {
      case WsConnection.Register(uids) =>
        uids.foreach { uid =>
          val entity = sharding.entityRefFor(Device.EntityKey, uid)
          devices = devices :+ entity
          entity ! Device.Register(context.self)
        }
        Behaviors.same
      case WsConnection.Notify(message) =>
        actorRef ! WsConnection.Notify(message)
        Behaviors.same
      case WsConnection.Connect(actorRef) =>
        this.actorRef = actorRef
        this.actorRef ! WsConnection.Connected(message = "conectado", userName = userName)
        Behaviors.same
      case WsConnection.Disconnected => {
        devices foreach { entity =>
          entity ! Device.UnRegister(context.self)
        }
        Behaviors.stopped
      }
    }
  }
}
