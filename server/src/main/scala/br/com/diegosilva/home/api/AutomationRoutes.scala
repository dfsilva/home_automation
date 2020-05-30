package br.com.diegosilva.home.api

import akka.NotUsed
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.ActorContext
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Route
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.typed.scaladsl.{ActorSink, ActorSource}
import akka.util.Timeout
import br.com.diegosilva.home.actors.WsConnection._
import br.com.diegosilva.home.actors.{Device, WsConnection}
import br.com.diegosilva.home.api.AutomationRoutes.SendMessage
import br.com.diegosilva.home.data.IOTMessage

import scala.concurrent.Future
import scala.concurrent.duration._

object AutomationRoutes {

  final case class SendMessage(id: String = "", sensor: String = "", value: String = "")

}


class AutomationRoutes()(implicit context: ActorContext[_]) {

  implicit private val timeout: Timeout =
    Timeout.create(context.system.settings.config.getDuration("automation.askTimeout"))
  private val sharding = ClusterSharding(context.system)

  import AutomationRoutes._
  import JsonFormats._
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import akka.http.scaladsl.server.Directives._
  import spray.json._

  val routes: Route = {

    pathPrefix("api") {
      concat(
        get {
          path("health") {
            complete(JsObject("message" -> JsString("Hello from " + context.system.address)))
          }
        },
        pathPrefix("device") {
          post {
            entity(as[SendMessage]) { data =>
              val entityRef = sharding.entityRefFor(Device.EntityKey, data.id)
              val reply: Future[Device.Response] =
                entityRef.ask(Device.Send(IOTMessage(id = data.id, sensor = data.sensor, value = data.value), 0, _))
              onSuccess(reply) {
                case Device.SendResponse(summary) =>
                  complete(StatusCodes.OK -> JsObject("message" -> JsString(summary)))
                case _ =>
                  complete(StatusCodes.BadRequest, JsObject("message" -> JsString("Erro ao enviar mensagem para dispositivo.")))
              }
            }
          }
        },
        path("ws" / Remaining) { userName: String =>
          handleWebSocketMessages(wsUser(userName, context))
        }
      )
    }
  }

  private def wsUser(userName: String, context: ActorContext[_]): Flow[Message, Message, NotUsed] = {

    val wsUser: ActorRef[WsConnection.Command] = context.spawnAnonymous(new WsConnection(userName).create())

    val sink: Sink[Message, NotUsed] =
      Flow[Message].collect {
        case TextMessage.Strict(string) => registerFormat.read(string.parseJson)
      }
        .to(ActorSink.actorRef[WsConnection.Command](ref = wsUser, onCompleteMessage = Disconnected, onFailureMessage = Fail))

    val source: Source[Message, NotUsed] =
      ActorSource.actorRef[WsConnection.Command](completionMatcher = {
        case Disconnected =>
      }, failureMatcher = {
        case Fail(ex) => ex
      }, bufferSize = 8, overflowStrategy = OverflowStrategy.fail)
        .map {
          case c: Notify => {
            context.log.info("Enviando mensagem {} para {}", c.toJson.toString(), userName)
            TextMessage.Strict(c.toJson.toString())
          }
          case c: Connected => {
            context.log.info("Enviando mensagem de conectado para usuario {}", userName)
            TextMessage.Strict(JsObject("message" -> JsString(c.message)).toString())
          }
        }
        .mapMaterializedValue({ wsHandler =>
          wsUser ! WsConnection.Connect(wsHandler)
          NotUsed
        })
        .keepAlive(maxIdle = 10.seconds, () => TextMessage.Strict("{\"message\": \"keep-alive\"}"))

    Flow.fromSinkAndSource(sink, source)
  }


}

object JsonFormats {

  import spray.json._
  import DefaultJsonProtocol._

  implicit val iotMessage: RootJsonFormat[IOTMessage] = jsonFormat3(IOTMessage.apply)
  implicit val sendMessageFormat: RootJsonFormat[SendMessage] = jsonFormat3(SendMessage)
  implicit val registerFormat: RootJsonFormat[Register] = jsonFormat1(Register)
  implicit val notifyFormat: RootJsonFormat[Notify] = jsonFormat1(Notify)

}
