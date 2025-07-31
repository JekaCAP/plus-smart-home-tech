package ru.yandex.practicum.service;

import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.hub.HubEvent;

/**
 * Интерфейс сервиса для обработки телеметрических событий
 * и отправки их в Kafka.
 */
public interface CollectorService {

    /**
     * Обрабатывает событие от сенсора и отправляет его в соответствующий топик Kafka.
     *
     * @param sensorEvent событие от сенсора (например, изменение температуры, движение и т.д.),
     *                    содержащее данные телеметрии
     */
    void sendSensorEvent(SensorEvent sensorEvent);

    /**
     * Обрабатывает событие, связанное с хабом, и отправляет его в соответствующий топик Kafka.
     *
     * @param hubEvent событие от хаба (например, регистрация или удаление устройства)
     */
    void sendHubEvent(HubEvent hubEvent);
}