package ru.yandex.practicum.handlers.sensor.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.handlers.sensor.AbstractSensorEventHandler;
import ru.yandex.practicum.mapper.ProtobufToAvroMapper;

@Component
public class TemperatureSensorGrpcHandler extends AbstractSensorEventHandler {

    public TemperatureSensorGrpcHandler(ProtobufToAvroMapper protobufToAvroMapper, KafkaEventProducer producer) {
        super(protobufToAvroMapper, producer);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }
}