package br.com.diegosilva.home.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import br.com.diegosilva.home.CborSerializable

object WsConnCreatorActor {

  sealed trait Command extends CborSerializable
  sealed trait Response extends Command

  final case class CreateWsCon(userId: String, replyTo: ActorRef[Created]) extends Command

  final case class Created(userActor: ActorRef[WsConnectionActor.Command]) extends Response

  def apply(): Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage[Command] {
        case CreateWsCon(userId, replyTo) =>
          val userWsCon: ActorRef[WsConnectionActor.Command] = context.spawnAnonymous(new WsConnectionActor(userId).create())
          replyTo.tell(Created(userWsCon))
          Behaviors.same
      }
    }
}