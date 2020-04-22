package br.com.diegosilva.home.actors

import akka.actor.Cancellable
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, PreRestart, Signal}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityTypeKey}
import br.com.diegosilva.home.CborSerializable
import br.com.diegosilva.home.actors.Device.{Command, Processar, Send}
import br.com.diegosilva.home.dto.IOTMessage
import br.com.diegosilva.home.factory.SerialPortFactory
import jssc.SerialPort

import scala.concurrent.duration._

object Device {

  sealed trait Command extends CborSerializable

  sealed trait Response extends CborSerializable

  final case class Processar(message: IOTMessage) extends Command

  final case class Send(message: IOTMessage, replyTo: ActorRef[Response], times: Int) extends Command

  final case class SendResponse(message: String) extends Response


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

  private var cancellable: Cancellable = null
  private var serialPort: SerialPort = SerialPortFactory.get(context.system.settings.config.getString("serial.port"))

  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case Processar(message) => {
        context.log.info("Processando mensagem IOT {}", message)
        Behaviors.same
      }
      case Send(message, replyTo, times) => {
        if (cancellable != null)
          cancellable.cancel()
        context.log.info("Escrevendo mensagem na serial {}", message.encode)
        serialPort.writeString(message.encode + "\n")
        if (times < 3) retry(2.seconds, Device.Send(message, null, times + 1))
        if (replyTo != null) replyTo ! Device.SendResponse("Mensagem enviada")
        Behaviors.same
      }
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = {
    case restart: PreRestart => {
      context.log.debug("Reiniciando de recebimento de leituras")
      this.serialPort = SerialPortFactory.get(context.system.settings.config.getString("serial.port"))
      Behaviors.same
    }
  }

  private def retry(duration: FiniteDuration, cmd: Device.Command): Unit = {
    if (this.cancellable != null) this.cancellable.cancel
    this.cancellable = context.scheduleOnce(duration, context.self, cmd)
  }
}