package ru.yandex.practicum.source.kafka.handler.sensor;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;
import ru.yandex.practicum.source.kafka.handler.KafkaConfig;

@Service
public class MotionSensorEventHandler extends SensorEventHandler<MotionSensorAvro> {
    public MotionSensorEventHandler(KafkaConfig.KafkaEventProducer producer, KafkaConfig kafkaTopics) {
        super(producer, kafkaTopics);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }

    @Override
    protected MotionSensorAvro mapToAvro(SensorEvent event) {
        var motionEvent = (MotionSensorEvent) event;
        return new MotionSensorAvro(
                motionEvent.getLinkQuality(),
                motionEvent.isMotion(),
                motionEvent.getVoltage()
        );
    }
}