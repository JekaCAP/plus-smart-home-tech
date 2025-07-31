package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.config.CollectorKafkaProperties;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectorServiceImpl implements CollectorService {

    private final CollectorKafkaProperties collectorProps;
    private final KafkaEventSender eventSender;
    private final SensorEventHandler sensorHandler;
    private final HubEventHandler hubHandler;

    @Override
    public void sendSensorEvent(SensorEvent sensorEvent) {
        SensorEventAvro avro = sensorHandler.mapToAvro(sensorEvent);
        eventSender.send(collectorProps.getTopics().getSensorsEvents(), avro.getHubId(), avro.getTimestamp().toEpochMilli(), avro);
    }

    @Override
    public void sendHubEvent(HubEvent hubEvent) {
        HubEventAvro avro = hubHandler.mapToAvro(hubEvent);
        eventSender.send(collectorProps.getTopics().getHubsEvents(), avro.getHubId(), avro.getTimestamp().toEpochMilli(), avro);
    }
}