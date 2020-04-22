package br.com.diegosilva.home.api

import akka.NotUsed
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.ActorContext
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives.{handleWebSocketMessages, path, _}
import akka.http.scaladsl.server.PathMatchers.Remaining
import akka.http.scaladsl.server.Route
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.typed.scaladsl.{ActorSink, ActorSource}
import br.com.diegosilva.home.actors.UserWs
import br.com.diegosilva.home.actors.UserWs.{Calculate, Calculated, Fail, WsHandleDropped}
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._

import scala.concurrent.duration._

object AutomationRoutes {

}

class AutomationRoutes()(implicit context: ActorContext[_]) {

  private val sharding = ClusterSharding(context.system)

  val routes: Route = {
    path("ws" / Remaining) { userName: String =>
      handleWebSocketMessages(wsUser(userName, context))
    }
  }

  private def wsUser(userName: String, context: ActorContext[_]): Flow[Message, Message, NotUsed] = {

    val wsUser: ActorRef[UserWs.Command] = context.spawn(new UserWs(userName).create(), s"user-$userName")

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
        .keepAlive(maxIdle = 10.seconds, () => TextMessage.Strict("Mensagem de Keep-alive enviada pelo recipent websocket"))

    Flow.fromSinkAndSource(sink, source)
  }


}
