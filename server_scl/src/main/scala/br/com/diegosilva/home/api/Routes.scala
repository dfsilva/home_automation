package br.com.diegosilva.home.api


import akka.NotUsed
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.ActorContext
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.PathMatchers.Remaining
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Flow
import br.com.diegosilva.home.actors.UserWs


trait Routes {

  val context : ActorContext[Any]

  private def wsUser(userName: String): Flow[Message, Message, NotUsed] = {

    val wsUser : ActorRef[UserWs.Command] = context.spawn(UserWs(), s"user-$userName")

    
  }

  val routes : Route = path("ws" / Remaining){ userName: String =>


  }

}
