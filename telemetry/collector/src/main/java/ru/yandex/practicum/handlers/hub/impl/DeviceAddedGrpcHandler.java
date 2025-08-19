package ru.yandex.practicum.handlers.hub.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handlers.hub.GrpcHubEventHandler;
import ru.yandex.practicum.mapper.ProtobufToAvroMapper;
import ru.yandex.practicum.config.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
@RequiredArgsConstructor
@Getter
public class DeviceAddedGrpcHandler implements GrpcHubEventHandler {
    private final ProtobufToAvroMapper mapper;
    private final KafkaEventProducer producer;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        HubEventAvro avro = mapper.toAvro(event);
        producer.sendHubEvent(avro);
    }
}