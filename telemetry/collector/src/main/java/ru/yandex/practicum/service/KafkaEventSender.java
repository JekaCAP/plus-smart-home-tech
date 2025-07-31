package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventSender {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T> void send(String topic, String key, long timestamp, T event) {
        kafkaTemplate.send(topic, null, timestamp, key, event)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        log.info("Message sent to topic {}: offset={}", topic, result.getRecordMetadata().offset());
                    } else {
                        log.error("Error sending event to Kafka topic {}: {}", topic, exception.getMessage());
                    }
                });
    }
}