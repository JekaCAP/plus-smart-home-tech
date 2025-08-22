package ru.yandex.practicum.handlers.sensor.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.handlers.sensor.AbstractSensorEventHandler;
import ru.yandex.practicum.mapper.ProtobufToAvroMapper;

@Component
public class MotionSensorGrpcHandler extends AbstractSensorEventHandler {

    public MotionSensorGrpcHandler(ProtobufToAvroMapper mapper, KafkaEventProducer producer) {
        super(mapper, producer);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }
}