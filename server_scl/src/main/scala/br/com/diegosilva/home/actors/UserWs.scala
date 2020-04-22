package br.com.diegosilva.home.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import br.com.diegosilva.home.CborSerializable

object UserWs {

  sealed trait Command extends CborSerializable

  final case class Calculate(val1: Int, val2: Int) extends Command

  def apply(): Behavior[Command] = Behaviors.setup { context =>
    Behaviors.receiveMessage[Command] {
      case Calculate(val1, val2) =>
        Behaviors.same
    }
  }
}
