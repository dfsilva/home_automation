package br.com.diegosilva.home

import akka.Done
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior, SupervisorStrategy}
import br.com.diegosilva.home.actors.ReceptorActor.Start
import br.com.diegosilva.home.actors.{DeviceActor, SerialWriterSingletonActor, ReceptorActor, WsUserFactoryActor}
import br.com.diegosilva.home.api.{AutomationRoutes, AutomationServer}
import br.com.diegosilva.home.firebase.FirebaseSdk
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object Main extends App {
  FirebaseSdk.init
  val system = ActorSystem[Done](Guardian(), "Automation", ConfigFactory.load)
}

object Guardian {
  def apply(): Behavior[Done] = {
    Behaviors.setup[Done] { context =>
      DeviceActor.init(context.system)

      val httpPort = context.system.settings.config.getInt("automation.http.port")

      val wsConCreatorRef = context.spawn(WsUserFactoryActor(), "wsConCreator")

      val routes = new AutomationRoutes(context.system, wsConCreatorRef)

      new AutomationServer(routes.routes, httpPort, context.system).start()

      val isReceptor = context.system.settings.config.getBoolean("serial.receptor")

      if (isReceptor) {
        SerialWriterSingletonActor.init(context.system, context.system.settings.config.getInt("server.node"))
        val receptor = context.spawn(Behaviors.supervise(ReceptorActor.create())
          .onFailure(SupervisorStrategy.restartWithBackoff(1.seconds, 5.seconds, 0.5)), "receptor")
        receptor ! Start()
      }

      Behaviors.receiveMessage {
        case Done =>
          Behaviors.stopped
      }
    }
  }
}




