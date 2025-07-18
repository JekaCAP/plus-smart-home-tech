package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.exception.HubHandlerNotFound;
import ru.yandex.practicum.exception.SensorHandlerNotFound;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;
import ru.yandex.practicum.source.kafka.handler.hub.HubEventHandler;
import ru.yandex.practicum.source.kafka.handler.sensor.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class EventController {
    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    public EventController(Set<SensorEventHandler> sensorEventHandlers, Set<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }

    @PostMapping("/sensors")
    public void sensors(@Valid @RequestBody SensorEvent event) {
        log.info("🔌 Получено событие от сенсора: type={}, deviceId={}", event.getType(), event.getId());

        SensorEventHandler handler = sensorEventHandlers.get(event.getType());
        if (handler == null) {
            log.error("❌ SensorEventHandler не найден для типа: {}", event.getType());
            throw new SensorHandlerNotFound("Sensor event not found: " + event.getType());
        }

        log.debug("✅ Найден обработчик: {}", handler.getClass().getSimpleName());
        handler.handle(event);
    }

    @PostMapping("/hubs")
    public void hubs(@Valid @RequestBody HubEvent event) {
        log.info("🧠 Получено событие от хаба: type={}, hubId={}", event.getType(), event.getHubId());

        HubEventHandler handler = hubEventHandlers.get(event.getType());
        if (handler == null) {
            log.error("❌ HubEventHandler не найден для типа: {}", event.getType());
            throw new HubHandlerNotFound("The hub event was not found: " + event.getType());
        }

        log.debug("✅ Найден обработчик: {}", handler.getClass().getSimpleName());
        handler.handle(event);
    }
}