package br.com.diegosilva.automation;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.Behaviors;
import br.com.diegosilva.automation.actors.Receptor;
import br.com.diegosilva.automation.routes.AutomationRoutes;
import com.typesafe.config.ConfigFactory;

import java.time.Duration;

public class Main {
    public static void main(String[] args) throws Exception {
//        Kamon.init();
        ActorSystem.create(Guardian.create(), "Automation", ConfigFactory.load());
    }
}

class Guardian {
    static Behavior<Void> create() {
        return Behaviors.setup(context -> {
            ActorSystem<?> system = context.getSystem();
            AutomationServer.start(new AutomationRoutes(system).routes(), system.settings().config().getInt("automation.http.port"), system);

            boolean isReceptor = system.settings().config().getBoolean("serial.receptor");

            if (isReceptor) {
                ActorRef<Receptor.Command> receptor = context.spawn(Behaviors.supervise(Receptor.create())
                        .onFailure(SupervisorStrategy.restartWithBackoff(Duration.ofSeconds(1), Duration.ofSeconds(5), 0.5)), "receptor");
                receptor.tell(new Receptor.Start());
            }

            return Behaviors.empty();
        });
    }
}
