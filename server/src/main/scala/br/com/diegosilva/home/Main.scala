package br.com.diegosilva.home

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior, SupervisorStrategy}
import br.com.diegosilva.home.actors.Receptor.Start
import br.com.diegosilva.home.actors.{Device, Receptor}
import br.com.diegosilva.home.api.{AutomationRoutes, AutomationServer}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._


object Guardian {
  def apply(): Behavior[Nothing] = {
    Behaviors.setup[Nothing] { context =>
      Device.init(context.system)

      val httpPort = context.system.settings.config.getInt("automation.http.port")
      val routes = new AutomationRoutes()(context)
      new AutomationServer(routes.routes, httpPort, context.system).start()

      val isReceptor = context.system.settings.config.getBoolean("serial.receptor")

      if (isReceptor) {
        val receptor = context.spawn(Behaviors.supervise(Receptor.create())
          .onFailure(SupervisorStrategy.restartWithBackoff(1.seconds, 5.seconds, 0.5)), "receptor")
        receptor ! Start()
      }

      Behaviors.empty
    }
  }
}

object Main extends App {
  val system = ActorSystem[Nothing](Guardian(), "Automation", ConfigFactory.load)
}


