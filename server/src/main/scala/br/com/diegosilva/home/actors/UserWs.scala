package br.com.diegosilva.home.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef}
import br.com.diegosilva.home.CborSerializable
import br.com.diegosilva.home.dto.IOTMessage

object UserWs {

  sealed trait Command extends CborSerializable

  case class ConnectWsHandle(actorRef: ActorRef[Command]) extends Command

  case object WsHandleDropped extends Command

  case class Fail(ex: Throwable) extends Command

  final case class Notify(message: IOTMessage) extends Command

  final case class Register(uids: List[String]) extends Command

}

class UserWs(val userName: String) {

  private var actorRef: ActorRef[UserWs.Command] = null

  def create(): Behavior[UserWs.Command] = Behaviors.setup { context =>
    val sharding = ClusterSharding(context.system)
    var devices: List[EntityRef[Device.Command]] = List()

    Behaviors.receiveMessage[UserWs.Command] {
      case UserWs.Register(uids) =>
        uids.foreach { uid =>
          val entity = sharding.entityRefFor(Device.EntityKey, uid)
          devices = devices :+ entity
          entity ! Device.Register(context.self)
        }
        Behaviors.same
      case UserWs.Notify(message) =>
        actorRef ! UserWs.Notify(message)
        Behaviors.same
      case UserWs.ConnectWsHandle(actorRef) =>
        this.actorRef = actorRef
        Behaviors.same
      case UserWs.WsHandleDropped => {
        devices foreach { entity =>
          entity ! Device.UnRegister(context.self)
        }
        Behaviors.stopped
      }
    }
  }
}
