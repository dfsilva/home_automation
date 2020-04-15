package br.com.diegosilva.automation.routes;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Adapter;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.http.javadsl.server.Route;
import akka.serialization.jackson.JacksonObjectMapperProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

import static akka.http.javadsl.server.Directives.*;

public class AutomationRoutes {

    private final ActorSystem<?> system;
    private final ClusterSharding sharding;
    private final Duration timeout;
    private final ObjectMapper objectMapper;

    public AutomationRoutes(ActorSystem<?> system) {
        this.system = system;
        sharding = ClusterSharding.get(system);
        timeout = system.settings().config().getDuration("automation.askTimeout");
        objectMapper = JacksonObjectMapperProvider.get(Adapter.toClassic(system))
                .getOrCreate("jackson-json", Optional.empty());
    }


    public Route routes() {
        return pathPrefix("api", () ->
                concat(
                        get(() -> path("health", () -> {

                            return complete("Hello from " + system.address());
                        }))

                )
        );
    }


}
