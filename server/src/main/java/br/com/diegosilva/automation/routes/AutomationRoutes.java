package br.com.diegosilva.automation.routes;

import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Adapter;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.server.Route;
import akka.japi.JavaPartialFunction;
import akka.serialization.jackson.JacksonObjectMapperProvider;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import br.com.diegosilva.automation.actors.Device;
import br.com.diegosilva.automation.dto.IOTMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

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


    public Flow<Message, Message, NotUsed> deviceWs(String uid) {
        return
                Flow.<Message>create()
                        .collect(new JavaPartialFunction<Message, Message>() {
                            @Override
                            public Message apply(Message msg, boolean isCheck) {
                                if (isCheck) {
                                    if (msg.isText()) {
                                        return null;
                                    } else {
                                        throw noMatch();
                                    }
                                } else {
                                    return handleTextMessage(msg.asTextMessage());
                                }
                            }
                        });
    }

    public static TextMessage handleTextMessage(TextMessage msg) {
        if (msg.isStrict())
        {
            return TextMessage.create("Hello " + msg.getStrictText());
        } else {
            return TextMessage.create(Source.single("Hello ").concat(msg.getStreamedText()));
        }
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
                        path("ws", () -> parameter("uid", uid -> handleWebSocketMessages(deviceWs(uid))))
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
