package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.HubHandlerNotFound;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mapper.EventMapper;
import ru.yandex.practicum.model.hub.DeviceAddedEvent;
import ru.yandex.practicum.model.hub.DeviceRemovedEvent;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.ScenarioRemovedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventHandler {

    private final EventMapper eventMapper;

    public HubEventAvro mapToAvro(HubEvent event) {
        log.info("Mapping HubEvent to Avro: {}", event);

        return switch (event) {
            case DeviceAddedEvent e -> eventMapper.toAvro(e);
            case DeviceRemovedEvent e -> eventMapper.toAvro(e);
            case ScenarioAddedEvent e -> eventMapper.toAvro(e);
            case ScenarioRemovedEvent e -> eventMapper.toAvro(e);
            default -> {
                log.warn("Unsupported HubEvent type: {}", event.getType());
                throw new HubHandlerNotFound("Unsupported HubEvent: " + event.getType());
            }
        };
    }
}