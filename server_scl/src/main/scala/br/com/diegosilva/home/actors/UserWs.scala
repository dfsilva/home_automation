package br.com.diegosilva.home.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import br.com.diegosilva.home.CborSerializable
import br.com.diegosilva.home.actors.UserWs.{Calculate, Calculated, Command, ConnectWsHandle}

object UserWs {

  sealed trait Command extends CborSerializable

  final case class Calculate(val1: Int, val2: Int) extends Command

  case class ConnectWsHandle(actorRef: ActorRef[Command]) extends Command

  case object WsHandleDropped extends Command

  case class Fail(ex: Throwable) extends Command

  case class Calculated(description: String, answer: Option[Int], username: String) extends Command

}

class UserWs(val userName: String, var actorRef: ActorRef[Command] = null) {

  def create(): Behavior[Command] = Behaviors.setup { context =>
    Behaviors.receiveMessage[Command] {
      case Calculate(val1, val2) =>
        actorRef ! Calculated("resposta", Some(val1 + val2), userName)
        Behaviors.same
      case ConnectWsHandle(actorRef) =>
        this.actorRef = actorRef
        Behaviors.same
    }
  }
}
