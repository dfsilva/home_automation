package br.com.diegosilva.automation.routes;

import akka.NotUsed;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Adapter;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.server.Route;
import akka.serialization.jackson.JacksonObjectMapperProvider;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.typed.javadsl.ActorSink;
import akka.stream.typed.javadsl.ActorSource;
import br.com.diegosilva.automation.actors.Device;
import br.com.diegosilva.automation.actors.UserActor;
import br.com.diegosilva.automation.dto.IOTMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;
import static akka.http.javadsl.server.PathMatchers.segment;

public class AutomationRoutes {

    private final ActorSystem<?> system;
    private final ClusterSharding sharding;
    private final Duration timeout;
    private final ObjectMapper objectMapper;
    private final ActorContext context;

    public AutomationRoutes(ActorSystem<?> system, ActorContext context) {
        this.system = system;
        sharding = ClusterSharding.get(system);
        timeout = system.settings().config().getDuration("automation.askTimeout");
        objectMapper = JacksonObjectMapperProvider.get(Adapter.toClassic(system))
                .getOrCreate("jackson-json", Optional.empty());
        this.context = context;
    }


    public Flow<Message, Message, NotUsed> deviceWs(String uid) {


//        EntityRef<UserActor.Command> ref = sharding.entityRefFor(UserActor.TypeKey, uid);
        ActorRef<UserActor.Command> actorRef = context.spawn(UserActor.create(uid), "user-" + uid);
//        Sink<UserActor.Command, NotUsed> sink = ;


        Sink<Message, NotUsed> sink = Flow.create()
                .map((o1)-> new UserActor.Soma(1, 2))
//                .collect((message) -> {
//                    return new UserActor.Soma(1, 2);
//                })
                .to(ActorSink.actorRef(actorRef, new UserActor.Desconectado(), UserActor.Falhou::new));


        Source<Message, NotUsed> source =
                ActorSource.actorRef((m) -> m instanceof UserActor.Response, (p) -> Optional.empty(), 8, OverflowStrategy.dropBuffer())
                        .map((m) -> {
                            return TextMessage.create("fasdfasd");
                        })
                        .mapMaterializedValue((wsHandler) -> {
                            actorRef.tell(new UserActor.Conectar(wsHandler));
                            return NotUsed.getInstance();
                        })
                        .keepAlive(Duration.ofSeconds(10), () -> TextMessage.create("Keep alive message"));

        return Flow.fromSinkAndSource(sink, source);
    }


    public Route routes() {
        return pathPrefix("api", () ->
                        concat(
                                get(() -> path("health", () -> {

                                    return complete("Hello from " + system.address());
                                })),
                                pathPrefix("device", () ->
                                        concat(
                                                post(() ->
                                                        entity(
                                                                Jackson.unmarshaller(objectMapper, IOTMessage.class),
                                                                data -> onConfirmationReply(sendMessage(data))))
                                        )
                                ),
                                path(segment("ws").slash(segment()), (uid) -> handleWebSocketMessages(deviceWs(uid)))
//                        path("ws", () ->
//                                handleWebSocketMessages(deviceWs("uid"))
//                        )
//                        path("ws", () -> parameter("uid", uid -> handleWebSocketMessages(deviceWs(uid))))
                        )
        );
    }


    private Route onConfirmationReply(CompletionStage<Device.Response> reply) {
        return onSuccess(reply, confirmation -> {
            if (confirmation instanceof Device.SendResponse)
                return complete(StatusCodes.OK, ((Device.SendResponse) confirmation).message, Jackson.marshaller(objectMapper));
            else
                return complete(StatusCodes.BAD_REQUEST, "Erro");
        });
    }


    private CompletionStage<Device.Response> sendMessage(IOTMessage data) {
        system.log().info("Enviando mensagem para entity {}", data.id);
        EntityRef<Device.Command> entityRef =
                sharding.entityRefFor(Device.TypeKey, data.id);
        return entityRef.ask(replyTo -> new Device.Send(data, 0, replyTo), timeout);
    }


}
