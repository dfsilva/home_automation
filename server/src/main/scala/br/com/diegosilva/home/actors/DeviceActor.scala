package br.com.diegosilva.home.actors

import akka.actor.typed._
import akka.actor.typed.pubsub.Topic
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityTypeKey}
import br.com.diegosilva.home.CborSerializable
import br.com.diegosilva.home.actors.DeviceActor._
import br.com.diegosilva.home.data.{IOTMessage, InterfaceType, Lecture}
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success}

object DeviceActor {

  sealed trait Command extends CborSerializable

  sealed trait Response extends CborSerializable

  final case class Processar(message: IOTMessage) extends Command

  final case class Send(message: IOTMessage, times: Int, replyTo: ActorRef[Response]) extends Command

  final case class SendResponse(message: String) extends Response

  val EntityKey: EntityTypeKey[Command] = EntityTypeKey[Command]("device")

  def init(system: ActorSystem[_]): Unit = {
    ClusterSharding(system).init(Entity(EntityKey) { entityContext =>
      create(entityContext.entityId)
    })
  }

  def create(deviceid: String): Behavior[Command] =
    Behaviors.setup(context => new DeviceActor(context, deviceid))

}

class DeviceActor(context: ActorContext[Command], val entityId: String) extends AbstractBehavior[Command](context) {

  private val log: Logger = LoggerFactory.getLogger(DeviceActor.getClass)

  private val interface = InterfaceType.withName(context.system.settings.config.getString("serial.interface"))
  private val writerTopic = interface match {
    case InterfaceType.RF24 => context.spawn(Topic[SerialWriterSingletonActor.TopicMessage](Topics.RF24TOPIC), "RF24TOPIC")
    case InterfaceType.RXTX => context.spawn(Topic[SerialWriterSingletonActor.TopicMessage](Topics.RXTXTOPIC), "RXTXTOPIC")
  }
  private val deviceTopic = context.spawn(Topic[WsUserActor.Notify](s"notify_$entityId"), s"notify_$entityId")

  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case Processar(message) => {
        Lecture.fromIotMessage(message) match {
          case Success(lecture) => {
            log.info("Enviando mensagem para o topico {} ", lecture)
            deviceTopic ! Topic.Publish(WsUserActor.Notify(lecture))
          }
          case Failure(_) => ()
        }
        Behaviors.same
      }
      case Send(message, times, replyTo) => {
        writerTopic ! Topic.Publish(SerialWriterSingletonActor.TopicMessage(message))
        //        if (cancellable != null)
        //          cancellable.cancel()
        //        context.log.info("Escrevendo mensagem na serial {}", message.encode)
        //        serialInterface.send(0, message.encode + "\n")
        //        if (times < 3) retry(2.seconds, Device.Send(message, times + 1, null))
        //        if (replyTo != null) replyTo ! Device.SendResponse("Mensagem enviada")
        Behaviors.same
      }
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = {
    case restart: PreRestart => {
      log.debug("Reiniciando de recebimento de leituras")
      //      this.serialInterface = SerialPortFactory.get(context.system.settings.config)
      Behaviors.same
    }
  }

  //  private def retry(duration: FiniteDuration, cmd: Device.Command): Unit = {
  ////    if (this.cancellable != null) this.cancellable.cancel
  ////    this.cancellable = context.scheduleOnce(duration, context.self, cmd)
  //  }
}