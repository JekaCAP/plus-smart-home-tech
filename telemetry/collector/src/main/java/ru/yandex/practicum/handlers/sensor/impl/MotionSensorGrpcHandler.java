package ru.yandex.practicum.handlers.sensor.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.handlers.sensor.GrpcSensorEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mapper.ProtobufToAvroMapper;

@Component
@RequiredArgsConstructor
@Getter
public class MotionSensorGrpcHandler implements GrpcSensorEventHandler {
    private final ProtobufToAvroMapper mapper;
    private final KafkaEventProducer producer;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        SensorEventAvro avro = mapper.toAvro(event);
        producer.sendSensorEvent(avro);
    }
}