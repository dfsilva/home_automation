package br.com.diegosilva.home.actors

import akka.actor.typed._
import akka.actor.typed.pubsub.Topic
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityTypeKey}
import br.com.diegosilva.home.CborSerializable
import br.com.diegosilva.home.actors.Device._
import br.com.diegosilva.home.actors.TopicMessages.SendTopic
import br.com.diegosilva.home.data.{IOTMessage, InterfaceType}

object Device {

  sealed trait Command extends CborSerializable

  sealed trait Response extends CborSerializable

  final case class Processar(message: IOTMessage) extends Command

  final case class Send(message: IOTMessage, times: Int, replyTo: ActorRef[Response]) extends Command

  final case class SendResponse(message: String) extends Response

  final case class Register(actorRef: ActorRef[WsConnection.Command]) extends Command

  final case class UnRegister(actorRef: ActorRef[WsConnection.Command]) extends Command

  val EntityKey: EntityTypeKey[Command] = EntityTypeKey[Command]("device")

  def init(system: ActorSystem[_]): Unit = {
    ClusterSharding(system).init(Entity(EntityKey) { entityContext =>
      create(entityContext.entityId)
    })
  }

  def create(deviceid: String): Behavior[Command] =
    Behaviors.setup(context => new Device(context, deviceid))

}

class Device(context: ActorContext[Command], val entityId: String) extends AbstractBehavior[Command](context) {

  private var registers: List[ActorRef[WsConnection.Command]] = List()
  private val interface: InterfaceType = context.system.settings.config.getEnum(Class[InterfaceType], "serial.interface")
  private val topic = interface match {
    case InterfaceType.rf24 => context.spawn(Topic[SendTopic](TopicMessages.RF24TOPIC), "RF24TOPIC")
    case InterfaceType.rxtx => context.spawn(Topic[SendTopic](TopicMessages.RXTXTOPIC), "RXTXTOPIC")
  }

  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case Processar(message) => {
        context.log.info("Processando mensagem IOT {} registers {}", message, registers.size)
        registers.foreach { actorRef =>
          actorRef ! WsConnection.Notify(message)
        }
        Behaviors.same
      }
      case Register(actorRef) => {
        registers = registers :+ actorRef
        Behaviors.same
      }
      case UnRegister(actorRef) => {
        registers = registers filter { value =>
          value.path != actorRef.path
        }
        Behaviors.same
      }
      case Send(message, times, replyTo) => {
        topic ! Topic.Publish(TopicMessages.SendTopic(message))
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
      context.log.debug("Reiniciando de recebimento de leituras")
      //      this.serialInterface = SerialPortFactory.get(context.system.settings.config)
      Behaviors.same
    }
  }

  //  private def retry(duration: FiniteDuration, cmd: Device.Command): Unit = {
  ////    if (this.cancellable != null) this.cancellable.cancel
  ////    this.cancellable = context.scheduleOnce(duration, context.self, cmd)
  //  }
}