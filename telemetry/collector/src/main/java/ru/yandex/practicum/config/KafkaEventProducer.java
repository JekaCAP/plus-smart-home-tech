package ru.yandex.practicum.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TelemetryKafkaProducerConfig config;

    public void sendHubEvent(HubEventAvro avroHubEvent) {
        String hubTopic = config.getHubTopic();

        log.info("В топик: {} отправляется событие хаба: {}", hubTopic, avroHubEvent);

        long eventTimestamp = avroHubEvent.getTimestamp().toEpochMilli();

        kafkaTemplate.send(hubTopic, null, eventTimestamp, avroHubEvent.getHubId(), avroHubEvent)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        log.info("Событие хаба: {} для hubId: {} успешно отправлено, смещение: {}",
                                avroHubEvent, avroHubEvent.getHubId(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Не удалось отправить событие хаба: {} для hubId: {}: {}",
                                avroHubEvent, avroHubEvent.getHubId(), exception.getMessage());
                    }
                });
    }

    public void sendSensorEvent(SensorEventAvro avroSensorEvent) {
        String sensorTopic = config.getSensorTopic();

        log.info("В топик: {} отправляется сенсорное событие: {}", sensorTopic, avroSensorEvent);

        long eventTimestamp = avroSensorEvent.getTimestamp().toEpochMilli();

        kafkaTemplate.send(sensorTopic, null, eventTimestamp, avroSensorEvent.getHubId(), avroSensorEvent)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        log.info("Событие сенсора: {} для hubId: {} успешно отправлено, смещение: {}",
                                avroSensorEvent, avroSensorEvent.getHubId(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Не удалось отправить событие сенсора: {} для hubId: {}: {}",
                                avroSensorEvent, avroSensorEvent.getHubId(), exception.getMessage());
                    }
                });
    }
}