package ru.practicum.aggregator.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

/**
 * Продюсер Kafka для отправки снапшотов состояния умного дома.
 * <p>
 * Использует {@link KafkaTemplate} для асинхронной отправки сообщений в топик.
 * Ключом сообщения выступает hubId, значением — {@link SensorsSnapshotAvro}.
 * Логи фиксируют успешные отправки и ошибки.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmartHomeSnapshotProducer {

    private final KafkaTemplate<String, SensorsSnapshotAvro> kafkaTemplate;

    @Value("${aggregator.kafka.topic.snapshots}")
    private String snapshotsTopic;

    /**
     * Отправляет снапшот в Kafka.
     * <p>
     * Логируется отправка, а также результат операции (успешно/с ошибкой).
     *
     * @param snapshot снапшот датчиков конкретного хаба
     */
    public void send(SensorsSnapshotAvro snapshot) {
        log.info("Начинаем отправку снапшота хаба '{}' в топик '{}'", snapshot.getHubId(), snapshotsTopic);

        kafkaTemplate.send(snapshotsTopic, snapshot.getHubId(), snapshot)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Снапшот успешно отправлен: hubId='{}', partition={}, offset={}",
                                snapshot.getHubId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Ошибка при отправке снапшота хаба '{}': {}", snapshot.getHubId(), ex.getMessage(), ex);
                    }
                });
    }
}