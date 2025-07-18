package ru.yandex.practicum.source.kafka.handler.sensor;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;
import ru.yandex.practicum.source.kafka.handler.KafkaConfig;

@Service
public class SwitchSensorEventHandler extends SensorEventHandler<SwitchSensorAvro> {
    public SwitchSensorEventHandler(KafkaConfig.KafkaEventProducer producer, KafkaConfig kafkaTopics) {
        super(producer, kafkaTopics);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

    @Override
    protected SwitchSensorAvro mapToAvro(SensorEvent event) {
        var switchEvent = (SwitchSensorEvent) event;
        return new SwitchSensorAvro(
                switchEvent.isState()
        );
    }
}