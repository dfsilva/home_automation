package br.com.diegosilva.home.api

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import akka.NotUsed
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive1, Route}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.typed.scaladsl.{ActorSink, ActorSource}
import akka.util.Timeout
import br.com.diegosilva.home.actors.WsUserActor._
import br.com.diegosilva.home.actors.WsUserFactoryActor.Created
import br.com.diegosilva.home.actors.{DeviceActor, WsUserActor, WsUserFactoryActor}
import br.com.diegosilva.home.data.{DeviceType, IOTMessage}
import br.com.diegosilva.home.database.DatabasePool
import br.com.diegosilva.home.repositories._
import com.google.firebase.auth.FirebaseAuth
import org.slf4j.{Logger, LoggerFactory}
import slick.dbio.DBIOAction

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

object AutomationRoutes {

  final case class SendMessage(id: String = "", sensor: String = "", value: String = "")

  final case class AddDevice(name: String, devType: String, address: String)

}

class AutomationRoutes(system: ActorSystem[_], wsConCreator: ActorRef[WsUserFactoryActor.Command]) extends JsonFormats {

  implicit val timeout: Timeout = Timeout.create(system.settings.config.getDuration("automation.askTimeout"))
  implicit val scheduler = system.scheduler
  implicit val executionContext = system.executionContext

  private val sharding = ClusterSharding(system)
  protected val db = DatabasePool(system).database

  private val log: Logger = LoggerFactory.getLogger(AutomationRoutes.getClass)

  private def getAuthToken(token: String): Future[AuthToken] = {
    val action = for {
      authOptToken <- AuthTokenRepo.getByToken(token)
      authToken <- authOptToken match {
        case Some(authT) if authT.expires.isAfter(LocalDateTime.now()) => DBIOAction.successful(authT)
        case Some(authT) if authT.expires.isBefore(LocalDateTime.now()) => {
          Try(FirebaseAuth.getInstance.verifyIdToken(token)) match {
            case Success(_) => {
              val updated = authT.copy(expires = LocalDateTime.now.plus(30, ChronoUnit.MINUTES))
              for (_ <- AuthTokenRepo.insertOrUpdate(updated)) yield authT
            }
            case Failure(ex) => DBIOAction.failed(ex)
          }
        }
        case _ => {
          Try(FirebaseAuth.getInstance.verifyIdToken(token)) match {
            case Success(ftoken) => {
              val authToken = AuthToken(ftoken.getUid, token, LocalDateTime.now(), LocalDateTime.now.plus(30, ChronoUnit.MINUTES))
              for (_ <- AuthTokenRepo.insertOrUpdate(authToken)) yield authToken
            }
            case Failure(ex) => DBIOAction.failed(ex)
          }
        }
      }
    } yield authToken
    db.run(action)
  }

  private def authenticated: Directive1[AuthToken] =
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(token) =>
        onComplete(getAuthToken(token.split(" ").last)) flatMap {
          case Success(authToken) => provide(authToken)
          case Failure(ex) => complete(StatusCodes.Unauthorized -> s"Invalid token ${ex.getMessage}")
        }
      case _ => complete(StatusCodes.Unauthorized -> "You have to be autenticated")
    }

  import AutomationRoutes._
  import akka.http.scaladsl.server.Directives._
  import spray.json._

  val routes: Route = {
    pathPrefix("api") {
      concat(
        get {
          path("health") {
            complete(JsObject("message" -> JsString("Olá de " + system.address)))
          }
        },
        path("ws" / Remaining) { userName: String =>
          log.info("criando o websocket")
          handleWebSocketMessages(wsUser(userName))
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
                      DeviceRepo.add(Device(name = data.name,
                        address = data.address,
                        devType = DeviceType.withName(data.devType),
                        owner = authData.userId,
                        position = 10))
                    })
                  }
                },
                get {
                  path("user" / Segment) { uid: String =>
                    val action = for {
                      devices <- DeviceRepo.devicesAndSensorsByUser(uid)
                      dev <- DBIOAction.successful(devices.groupBy(_._1)
                        .map(grouped => DeviceSensors(grouped._1, grouped._2
                          .map(sensors => sensors._2))).toSeq)
                    } yield dev

                    complete(db.run {
                      action
                    })
                  }
                }
              )
            },
            pathPrefix("users") {
              concat(
                get {
                  path(Segment) { uid: String =>
                    complete(db.run {
                      UserRepo.load(uid)
                    })
                  }
                }
              )
            }
          )
        }
      )
    }
  }

  private def wsUser(userId: String): Flow[Message, Message, NotUsed] = {

    //    val wsUser: ActorRef[WsConnectionActor.Command] = Await.result(spawn.spawn(new WsConnectionActor(userId).create(), userId), 2.seconds)

    log.info("Criando websocket")

    val wsConCreated: Created = Await.result(wsConCreator.ask(replyTo => WsUserFactoryActor.CreateWsCon(userId, replyTo)), 2.seconds)
    val wsUser = wsConCreated.userActor

    val sink: Sink[Message, NotUsed] =
      Flow[Message].collect {
        case TextMessage.Strict(string) => {
          log.info("Recebido mensagem de texto {}", string)
          val registerMsg = registerFormat.read(string.parseJson)
          registerMsg
        }
      }
        .to(ActorSink.actorRef[WsUserActor.Command](ref = wsUser, onCompleteMessage = Disconnected, onFailureMessage = Fail))

    val source: Source[Message, NotUsed] =
      ActorSource.actorRef[WsUserActor.Command](completionMatcher = {
        case Disconnected => {
          log.error("Disconected")
        }
      }, failureMatcher = {
        case Fail(ex) => ex
      }, bufferSize = 8, overflowStrategy = OverflowStrategy.fail)
        .map {
          case c: Notify => {
            log.error("Enviando mensagem {} para {}", c.toJson.toString(), userId)
            TextMessage.Strict(c.toJson.toString())
          }
          case c: Connected => {
            log.error("Enviando mensagem de conectado para usuario {}", userId)
            TextMessage.Strict(JsObject("message" -> JsString(c.message)).toString())
          }
          case _ => {
            TextMessage.Strict("Mensagem não reconhecida")
          }
        }
        .mapMaterializedValue({ wsHandler =>
          log.info("Conectando")
          wsUser ! WsUserActor.Connect(wsHandler)
          NotUsed
        })
        .keepAlive(maxIdle = 10.seconds, () => TextMessage.Strict("{\"message\": \"keep-alive\"}"))

    Flow.fromSinkAndSource(sink, source)
  }


}

