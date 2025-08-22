package ru.yandex.practicum.handlers.hub.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.handlers.hub.AbstractHubEventHandler;
import ru.yandex.practicum.mapper.ProtobufToAvroMapper;

@Component
public class ScenarioAddedGrpcHandler extends AbstractHubEventHandler {

    public ScenarioAddedGrpcHandler(ProtobufToAvroMapper mapper, KafkaEventProducer producer) {
        super(mapper, producer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

}