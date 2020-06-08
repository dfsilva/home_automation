package br.com.diegosilva.home.api

import java.time.LocalDateTime

import akka.NotUsed
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.ActorContext
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive1, Route}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.typed.scaladsl.{ActorSink, ActorSource}
import akka.util.Timeout
import br.com.diegosilva.home.actors.WsConnectionActor._
import br.com.diegosilva.home.actors.{DeviceActor, WsConnectionActor}
import br.com.diegosilva.home.api.AutomationRoutes.{AddDevice, SendMessage}
import br.com.diegosilva.home.data.{DeviceType, IOTMessage}
import br.com.diegosilva.home.database.DatabasePool
import br.com.diegosilva.home.repositories.{AuthToken, AuthTokenRepo, Device, DeviceRepo}
import com.google.firebase.auth.{FirebaseAuth, FirebaseToken}

import scala.concurrent.Future
import scala.concurrent.duration._

object AutomationRoutes {

  final val Name = "http-api"
  final val AccessTokenHeaderName = "X-Access-Token"

  final case class SendMessage(id: String = "", sensor: String = "", value: String = "")

  final case class AddDevice(name: String, devType: String)

}

class AutomationRoutes()(implicit context: ActorContext[_]) {

  implicit private val timeout: Timeout = Timeout.create(context.system.settings.config.getDuration("automation.askTimeout"))
  implicit val executionContext = context.executionContext
  private val sharding = ClusterSharding(context.system)
  protected val db = DatabasePool(context.system).database

  private def getAuthToken(token: String): Future[AuthToken] = {
    db.run {
      AuthTokenRepo.getByToken(token)
    } map { result =>
      result match {
        case Some(authToken) => authToken
        case None => {
          val ftoken: FirebaseToken = FirebaseAuth.getInstance.verifyIdToken(token)
          db.run(AuthTokenRepo.add(AuthToken(userId = ftoken.getUid,
            token = token,
            created = LocalDateTime.now(),
            expires = LocalDateTime.now().plusMinutes(30))))
        }
      }
    }
  }

  private def isTokenExpired(token: String): Boolean = false

  private def isTokenValid(token: String): Boolean = true

  private def getAuthData(token: String): Map[String, String] = {

  }

  private def authenticated: Directive1[Map[String, Any]] =
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(token) if isTokenExpired(token) =>
        complete(StatusCodes.Unauthorized -> "Token expired.")
      case Some(token) if isTokenValid(token) =>
        provide(getAuthData(token))
      case _ => complete(StatusCodes.Unauthorized)
    }

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
        authenticated { authData =>
          concat(
            pathPrefix("device") {
              concat(
                pathPrefix("send") {
                  post {
                    entity(as[SendMessage]) { data =>
                      val entityRef = sharding.entityRefFor(DeviceActor.EntityKey, data.id)
                      val reply: Future[DeviceActor.Response] =
                        entityRef.ask(DeviceActor.Send(IOTMessage(id = data.id, sensor = data.sensor, value = data.value), 0, _))
                      onSuccess(reply) {
                        case DeviceActor.SendResponse(summary) =>
                          complete(StatusCodes.OK -> JsObject("message" -> JsString(summary)))
                        case _ =>
                          complete(StatusCodes.BadRequest, JsObject("message" -> JsString("Erro ao enviar mensagem para dispositivo.")))
                      }
                    }
                  }
                },
                post {
                  entity(as[AddDevice]) { data =>
                    complete(db.run {
                      DeviceRepo.add(Device(name = data.name, devType = DeviceType.withName(data.devType), owner = ""))
                    })
                  }
                }
              )
            },
            path("ws" / Remaining) { userName: String =>
              handleWebSocketMessages(wsUser(userName, context))
            }
          )
        }
      )
    }
  }

  private def wsUser(userName: String, context: ActorContext[_]): Flow[Message, Message, NotUsed] = {

    val wsUser: ActorRef[WsConnectionActor.Command] = context.spawnAnonymous(new WsConnectionActor(userName).create())

    val sink: Sink[Message, NotUsed] =
      Flow[Message].collect {
        case TextMessage.Strict(string) => registerFormat.read(string.parseJson)
      }
        .to(ActorSink.actorRef[WsConnectionActor.Command](ref = wsUser, onCompleteMessage = Disconnected, onFailureMessage = Fail))

    val source: Source[Message, NotUsed] =
      ActorSource.actorRef[WsConnectionActor.Command](completionMatcher = {
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
          wsUser ! WsConnectionActor.Connect(wsHandler)
          NotUsed
        })
        .keepAlive(maxIdle = 10.seconds, () => TextMessage.Strict("{\"message\": \"keep-alive\"}"))

    Flow.fromSinkAndSource(sink, source)
  }


}

object JsonFormats {

  import spray.json._
  import DefaultJsonProtocol._

  class EnumJsonConverter[T <: scala.Enumeration](enu: T) extends RootJsonFormat[T#Value] {
    override def write(obj: T#Value): JsValue = JsString(obj.toString)

    override def read(json: JsValue): T#Value = {
      json match {
        case JsString(txt) => enu.withName(txt)
        case somethingElse => throw DeserializationException(s"Expected a value from enum $enu instead of $somethingElse")
      }
    }
  }

  implicit val enumConverter = new EnumJsonConverter(DeviceType)
  implicit val iotMessage: RootJsonFormat[IOTMessage] = jsonFormat3(IOTMessage.apply)
  implicit val sendMessageFormat: RootJsonFormat[SendMessage] = jsonFormat3(SendMessage)
  implicit val addDeviceFormat: RootJsonFormat[AddDevice] = jsonFormat2(AddDevice)
  implicit val deviceFormat: RootJsonFormat[Device] = jsonFormat4(Device)
  implicit val registerFormat: RootJsonFormat[Register] = jsonFormat1(Register)
  implicit val notifyFormat: RootJsonFormat[Notify] = jsonFormat1(Notify)

}
