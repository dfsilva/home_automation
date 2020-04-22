package br.com.diegosilva.home


import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Adapter
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.stream.Materializer
import akka.{Done, actor}
import br.com.diegosilva.home.api.Routes

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object Main extends App with Routes {

  val system = ActorSystem[Any](Behaviors.setup { ctx =>

    implicit val classicSystem: actor.ActorSystem = Adapter.toClassic(ctx.system)
    implicit val materializer: Materializer = Materializer.matFromSystem(ctx.system)
    implicit val ec: ExecutionContextExecutor = ctx.system.executionContext

    val serverBinding: Future[Http.ServerBinding] = Http().bindAndHandle(routes(ctx), "localhost", 8080)

    serverBinding.onComplete {
      case Success(bound) =>
        println(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
      case Failure(e) =>
        Console.err.println(s"Server could not start!")
        e.printStackTrace()
        ctx.self ! Done
    }
    Behaviors.receiveMessage {
      case Done =>
        Behaviors.stopped
    }
  }, "helloAkkaHttpServer")
}
