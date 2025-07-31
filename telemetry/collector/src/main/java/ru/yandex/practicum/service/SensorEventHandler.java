package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.SensorHandlerNotFound;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mapper.EventMapper;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.model.sensor.LightSensorEvent;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class SensorEventHandler {

    private final EventMapper eventMapper;

    public SensorEventAvro mapToAvro(SensorEvent event) {
        log.info("Mapping SensorEvent to Avro: {}", event);

        return switch (event) {
            case ClimateSensorEvent e -> eventMapper.toAvro(e);
            case LightSensorEvent e -> eventMapper.toAvro(e);
            case MotionSensorEvent e -> eventMapper.toAvro(e);
            case SwitchSensorEvent e -> eventMapper.toAvro(e);
            case TemperatureSensorEvent e -> eventMapper.toAvro(e);
            default -> {
                log.warn("Unsupported SensorEvent type: {}", event.getType());
                throw new SensorHandlerNotFound("Unsupported SensorEvent: " + event.getType());
            }
        };
    }
}