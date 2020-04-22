package br.com.diegosilva.home.actors

import akka.actor.Cancellable
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{Behavior, PreRestart, Signal}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef}
import br.com.diegosilva.home.CborSerializable
import br.com.diegosilva.home.actors.Receptor.{Command, Start}
import br.com.diegosilva.home.dto.IOTMessage
import br.com.diegosilva.home.factory.SerialPortFactory
import jssc.SerialPort

import scala.concurrent.duration._

object Receptor {

  sealed trait Command extends CborSerializable

  final case class Start() extends Command

  def apply(): Behavior[Command] =
    Behaviors.setup(context => new Receptor(context))

}

class Receptor(context: ActorContext[Command]) extends AbstractBehavior[Command](context) {

  private var serialPort: SerialPort = SerialPortFactory.get(context.system.settings.config.getString("serial.port"))
  private var cancellable: Cancellable = null

  override def onMessage(msg: Command): Behavior[Command] = {
    case Start =>
      context.log.debug("Iniciando escuta")
      startReceive()
  }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = {
    case restart: PreRestart => {
      context.log.debug("Reiniciando de recebimento de leituras")
      this.serialPort = SerialPortFactory.get(context.system.settings.config.getString("serial.port"))
      retry(5.seconds, new Receptor.Start)
      Behaviors.same
    }
  }

  private def startReceive() {
    val message = new StringBuilder()
    serialPort.addEventListener { event =>
      if (event.isRXCHAR && event.getEventValue > 0) {
        val buffer: Array[Byte] = serialPort.readBytes()
        buffer.foreach { b =>
          if ((b == '\n') && message.length > 0) {
            val toProcess = message.toString.replaceAll("[^a-zA-Z0-9:,;._]", "")
            context.log.info("Valor recebido {}", toProcess)
            message.setLength(0)
            val iotMessage = IOTMessage.decode(toProcess)
            iotMessage match {
              case Some(iotMessage) =>
                getDevice(iotMessage.id) ! Device.Processar(iotMessage)
            }
          }
          else message.append(b.toChar)
        }
      }
    }
  }

  private def getDevice(id: String): EntityRef[Device.Command] = {
    val sharding = ClusterSharding(context.system)
    sharding.entityRefFor(Device.EntityKey, id)
  }

  private def retry(duration: FiniteDuration, cmd: Receptor.Command): Unit = {
    if (this.cancellable != null) this.cancellable.cancel
    this.cancellable = context.scheduleOnce(duration, context.self, cmd)
  }
}
