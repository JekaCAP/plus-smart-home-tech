package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.CollectorService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class CollectorController {
    private final CollectorService collectorService;

    @PostMapping("/sensors")
    public void sendSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        log.info("Received sensor event: {}", sensorEvent);
        collectorService.sendSensorEvent(sensorEvent);
        log.info("Sensor event successfully processed: {}", sensorEvent);
    }

    @PostMapping("/hubs")
    public void sendHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        log.info("Received hub event: {}", hubEvent);
        collectorService.sendHubEvent(hubEvent);
        log.info("Hub event successfully processed: {}", hubEvent);
    }
}