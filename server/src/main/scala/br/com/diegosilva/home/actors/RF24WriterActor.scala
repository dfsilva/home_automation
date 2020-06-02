package br.com.diegosilva.home.actors

import akka.actor.Cancellable
import akka.actor.typed._
import akka.actor.typed.pubsub.Topic
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.cluster.typed.{ClusterSingleton, SingletonActor}
import br.com.diegosilva.home.CborSerializable
import br.com.diegosilva.home.actors.RF24WriterActor.{Start, WrappedBackendResponse}
import br.com.diegosilva.home.actors.TopicMessages.SendTopic
import br.com.diegosilva.home.factory.SerialPortFactory
import br.com.diegosilva.home.serial.SerialInterface

import scala.concurrent.duration._

object RF24WriterActor {

  sealed trait Command extends CborSerializable

  sealed trait Response extends CborSerializable

  final case class Start() extends Command

  final case class WrappedBackendResponse(response: TopicMessages.SendTopic) extends Command

  def init(system: ActorSystem[_], uid: String): Unit = {
    val proxy: ActorRef[RF24WriterActor.Command] = ClusterSingleton(system).init(
      SingletonActor(Behaviors.supervise(create())
        .onFailure[Exception](SupervisorStrategy.restart), s"Rf24Writter_$uid"))
    proxy ! RF24WriterActor.Start()
  }

  def create(): Behavior[Command] =
    Behaviors.setup(context => new RF24WriterActor(context))

}

class RF24WriterActor(context: ActorContext[RF24WriterActor.Command]) extends AbstractBehavior[RF24WriterActor.Command](context) {

  private var cancellable: Cancellable = null
  private var serialInterface: SerialInterface = SerialPortFactory.get(context.system.settings.config)

  private val topic = context.spawn(Topic[TopicMessages.SendTopic](TopicMessages.RF24TOPIC), "RF24TOPIC")

  private val mapper: ActorRef[TopicMessages.SendTopic] = context.messageAdapter(rsp => WrappedBackendResponse(rsp))

  override def onMessage(msg: RF24WriterActor.Command): Behavior[RF24WriterActor.Command] = {
    msg match {
      case Start() => {
        topic ! Topic.Subscribe(context.self)
        Behaviors.same
      }
      case wrapped: WrappedBackendResponse => {
        wrapped.response match {
          case SendTopic(iotMsg) => {
            if (cancellable != null)
              cancellable.cancel()
            context.log.info("Escrevendo mensagem na serial {}", iotMsg.encode)
            if (!serialInterface.send(Integer.parseInt(iotMsg.id), iotMsg.encode + "\n")) {
                context.log.error("Erro ao enviar!!!!!")
            }
            Behaviors.same
          }
        }
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

  override def onSignal: PartialFunction[Signal, Behavior[RF24WriterActor.Command]] = {
    case restart: PreRestart => {
      context.log.debug("Reiniciando de recebimento de leituras")
      this.serialInterface = SerialPortFactory.get(context.system.settings.config)
      Behaviors.same
    }
  }

  private def retry(duration: FiniteDuration, cmd: RF24WriterActor.Command): Unit = {
    if (this.cancellable != null) this.cancellable.cancel
    this.cancellable = context.scheduleOnce(duration, context.self, cmd)
  }
}
