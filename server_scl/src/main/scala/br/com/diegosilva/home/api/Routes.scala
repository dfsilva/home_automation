package br.com.diegosilva.home.api


import akka.NotUsed
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.ActorContext
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.Remaining
import akka.http.scaladsl.server.Route
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.typed.scaladsl.{ActorSink, ActorSource}
import br.com.diegosilva.home.actors.UserWs
import br.com.diegosilva.home.actors.UserWs.{Calculate, Calculated, Fail, WsHandleDropped}
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.concurrent.duration._


trait Routes {


  private def wsUser(userName: String, context: ActorContext[Any]): Flow[Message, Message, NotUsed] = {

    val wsUser: ActorRef[UserWs.Command] = context.spawn(UserWs(), s"user-$userName")

    val sink: Sink[Message, NotUsed] =
      Flow[Message].collect {
        case TextMessage.Strict(json) => decode[Calculate](json)
      }
        .filter(_.isRight)
        .map(_.getOrElse(null))
        .to(ActorSink.actorRef[UserWs.Command](ref = wsUser, onCompleteMessage = WsHandleDropped, onFailureMessage = Fail))


    val source: Source[Message, NotUsed] =
      ActorSource.actorRef[UserWs.Command](completionMatcher = {
        case WsHandleDropped =>
      }, failureMatcher = {
        case Fail(ex) => ex
      }, bufferSize = 8, overflowStrategy = OverflowStrategy.fail)
        .map {
          case c: Calculated => TextMessage.Strict(c.asJson.noSpaces)
        }
        .mapMaterializedValue({ wsHandler =>
          wsUser ! UserWs.ConnectWsHandle(wsHandler)
          NotUsed
        })
        .keepAlive(maxIdle = 10.seconds, () => TextMessage.Strict("Keep-alive message sent to WebSocket recipient"))

    Flow.fromSinkAndSource(sink, source)
  }

  def routes(context: ActorContext[Any]): Route = {
    path("ws" / Remaining) { userName: String =>
      handleWebSocketMessages(wsUser(userName, context))
    }
  }

}
