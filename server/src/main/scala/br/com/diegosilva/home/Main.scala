package br.com.diegosilva.home

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior, SupervisorStrategy}
import br.com.diegosilva.home.actors.ReceptorActor.Start
import br.com.diegosilva.home.actors.{DeviceActor, RF24WriterActor, ReceptorActor}
import br.com.diegosilva.home.api.{AutomationRoutes, AutomationServer}
import br.com.diegosilva.home.firebase.FirebaseSdk
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object Guardian {
  def apply(): Behavior[Nothing] = {
    Behaviors.setup[Nothing] { context =>
      DeviceActor.init(context.system)
      RF24WriterActor.init(context.system, "1")

      val httpPort = context.system.settings.config.getInt("automation.http.port")
      val routes = new AutomationRoutes()(context)
      new AutomationServer(routes.routes, httpPort, context.system).start()

      val isReceptor = context.system.settings.config.getBoolean("serial.receptor")

      if (isReceptor) {
        val receptor = context.spawn(Behaviors.supervise(ReceptorActor.create())
          .onFailure(SupervisorStrategy.restartWithBackoff(1.seconds, 5.seconds, 0.5)), "receptor")
        receptor ! Start()
      }

      Behaviors.empty
    }
  }
}

object Main extends App {
  FirebaseSdk.init
  val system = ActorSystem[Nothing](Guardian(), "Automation", ConfigFactory.load)
}


