package ru.practicum.analyzer.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.service.SmartHomeDirective;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

/**
 * Kafka consumer для получения снимков состояния сенсоров (SensorsSnapshotAvro)
 * из топика snapshot и передачи их в сервис обработки сценариев умного дома.
 * <p>
 * Использует {@link SmartHomeDirective} для обновления состояния устройств согласно
 * сценариям.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SnapshotConsumer {
    private final SmartHomeDirective smartHomeDirective;

    @KafkaListener(
            containerFactory = "snapshotKafkaListenerContainerFactory",
            topics = "${spring.kafka.snapshot.consumer-topics}"
    )
    public void listenSnapshot(SensorsSnapshotAvro snapshotAvro) {
        String hubId = snapshotAvro.getHubId();
        try {
            log.info("Получен снимок для hubId: {}", hubId);
            smartHomeDirective.update(snapshotAvro);
        } catch (Exception e) {
            log.error("Ошибка при обработке снимка для hubId: {}, ошибка: {}", hubId, e.getMessage(), e);
        }
    }
}