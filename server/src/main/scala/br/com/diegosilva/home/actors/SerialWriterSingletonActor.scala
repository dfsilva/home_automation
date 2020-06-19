package br.com.diegosilva.home.actors

import akka.actor.Cancellable
import akka.actor.typed._
import akka.actor.typed.pubsub.Topic
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.cluster.typed.{ClusterSingleton, SingletonActor}
import br.com.diegosilva.home.CborSerializable
import br.com.diegosilva.home.actors.SerialWriterSingletonActor.{Start, TopicMessage}
import br.com.diegosilva.home.data.{IOTMessage, InterfaceType}
import br.com.diegosilva.home.factory.SerialPortFactory
import br.com.diegosilva.home.serial.SerialInterface

import scala.concurrent.duration._

object Topics{
  final val RF24TOPIC:String = "rf24_topic_message"
  final val RXTXTOPIC:String = "rxtx_topic_message"
}

object SerialWriterSingletonActor {

  sealed trait Command extends CborSerializable

  sealed trait Response extends CborSerializable

  final case class TopicMessage(message: IOTMessage) extends Command

  final case class Start() extends Command

  def init(system: ActorSystem[_], node: Int): Unit = {
    val proxy: ActorRef[SerialWriterSingletonActor.Command] = ClusterSingleton(system).init(
      SingletonActor(Behaviors.supervise(create()).onFailure[Exception](SupervisorStrategy.restart), s"SerialWriter$node"))
    proxy ! SerialWriterSingletonActor.Start()
  }

  def create(): Behavior[Command] =
    Behaviors.setup(context => new SerialWriterSingletonActor(context))

}

class SerialWriterSingletonActor(context: ActorContext[SerialWriterSingletonActor.Command]) extends AbstractBehavior[SerialWriterSingletonActor.Command](context) {

  private var cancellable: Cancellable = null
  private var serialInterface: SerialInterface = SerialPortFactory.get(context.system.settings.config)

  private val interface = InterfaceType.withName(context.system.settings.config.getString("serial.interface"))

  private val topic = interface match {
    case InterfaceType.RF24 => context.spawn(Topic[TopicMessage](Topics.RF24TOPIC), "RF24TOPIC")
    case InterfaceType.RXTX => context.spawn(Topic[TopicMessage](Topics.RXTXTOPIC), "RXTXTOPIC")
  }

  override def onMessage(msg: SerialWriterSingletonActor.Command): Behavior[SerialWriterSingletonActor.Command] = {
    msg match {
      case Start() => {
        topic ! Topic.Subscribe(context.self)
        Behaviors.same
      }
      case TopicMessage(iotMsg) => {
        if (cancellable != null)
          cancellable.cancel()
        context.log.info("Escrevendo mensagem na serial {}", iotMsg.encode)
        if (!serialInterface.send(Integer.parseInt(iotMsg.id), iotMsg.encode + "\n")) {
          context.log.error("Erro ao enviar!!!!!")
        }
        Behaviors.same
      }
      //      case Send(message, times, replyTo) => {
      //        if (cancellable != null)
      //          cancellable.cancel()
      //        context.log.info("Escrevendo mensagem na serial {}", message.encode)
      //        serialInterface.send(0, message.encode + "\n")
      //        if (times < 3) retry(2.seconds, Rf24Writter.Send(message, times + 1, null))
      //        if (replyTo != null) replyTo ! Rf24Writter.SendResponse("Mensagem enviada")
      //        Behaviors.same
      //      }
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[SerialWriterSingletonActor.Command]] = {
    case restart: PreRestart => {
      context.log.debug("Reiniciando de recebimento de leituras")
      this.serialInterface = SerialPortFactory.get(context.system.settings.config)
      Behaviors.same
    }
  }

  private def retry(duration: FiniteDuration, cmd: SerialWriterSingletonActor.Command): Unit = {
    if (this.cancellable != null) this.cancellable.cancel
    this.cancellable = context.scheduleOnce(duration, context.self, cmd)
  }
}
