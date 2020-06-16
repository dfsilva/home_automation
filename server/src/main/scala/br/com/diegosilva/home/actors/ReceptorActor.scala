package br.com.diegosilva.home.actors

import akka.actor.Cancellable
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{Behavior, PreRestart, Signal}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef}
import br.com.diegosilva.home.CborSerializable
import br.com.diegosilva.home.actors.ReceptorActor.{Command, Start}
import br.com.diegosilva.home.data.IOTMessage
import br.com.diegosilva.home.factory.SerialPortFactory
import br.com.diegosilva.home.serial.SerialInterface

import scala.concurrent.duration._

object ReceptorActor {

  sealed trait Command extends CborSerializable

  final case class Start() extends Command

  def apply(): Behavior[Command] =
    Behaviors.setup(context => new ReceptorActor(context))

  def create(): Behavior[Command] = {
    Behaviors.setup(context => new ReceptorActor(context))
  }
}

class ReceptorActor(context: ActorContext[Command]) extends AbstractBehavior[Command](context) {

  private val sharding = ClusterSharding(context.system)
  private var serialInterface: SerialInterface = SerialPortFactory.get(context.system.settings.config)
  private var cancellable: Cancellable = null

  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case Start() =>
        context.log.debug("Iniciando listener para receber mensagens")
        startReceive()
        Behaviors.same
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = {
    case PreRestart => {
      context.log.debug("Reiniciando de recebimento de leituras")
      this.serialInterface = SerialPortFactory.get(context.system.settings.config)
      retry(5.seconds, new ReceptorActor.Start)
      Behaviors.same
    }
  }

  private def startReceive() {
    serialInterface.onReceive(toProcess => {
      val iotMessage = IOTMessage.decode(toProcess)
      iotMessage match {
        case Some(iotMessage) =>
          sharding.entityRefFor(DeviceActor.EntityKey, iotMessage.id) ! DeviceActor.Processar(iotMessage)
        case _ =>
      }
    })
  }

  private def retry(duration: FiniteDuration, cmd: ReceptorActor.Command): Unit = {
    if (this.cancellable != null) this.cancellable.cancel
    this.cancellable = context.scheduleOnce(duration, context.self, cmd)
  }
}
