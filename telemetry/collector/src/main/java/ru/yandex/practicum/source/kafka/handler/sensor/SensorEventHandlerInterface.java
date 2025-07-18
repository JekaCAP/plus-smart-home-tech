package ru.yandex.practicum.source.kafka.handler.sensor;

import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;

public interface SensorEventHandlerInterface {
    SensorEventType getMessageType();

    void handle(SensorEvent event);
}