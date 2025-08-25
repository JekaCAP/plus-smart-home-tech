package ru.practicum.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.config.SmartHomeKafkaConsumerConfig;
import ru.practicum.aggregator.kafka.SmartHomeSnapshotProducer;
import ru.practicum.aggregator.service.SmartHomeSnapshotService;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис запуска процесса агрегации событий датчиков.
 * <p>
 * Читает события из Kafka-топика, обновляет снапшоты состояния датчиков
 * для каждого хаба и отправляет их в топик с текущими снапшотами.
 * Управляет commit'ами оффсетов и корректным завершением работы consumer.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmartHomeAggregationEngine {

    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final SmartHomeSnapshotProducer producer;
    private final SmartHomeSnapshotService snapshotService;
    private final SmartHomeKafkaConsumerConfig config;

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new ConcurrentHashMap<>();

    /**
     * Запускает цикл агрегации событий.
     * <p>
     * Подписывается на топик с событиями датчиков, обновляет снапшоты и
     * отправляет их в Kafka. Включает корректное завершение работы через shutdown hook.
     * </p>
     */
    public void start() {
        log.info("Инициализация SmartHomeAggregationEngine. Kafka consumer готов к работе.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Получен сигнал завершения приложения. Пробуждаем consumer для остановки цикла.");
            consumer.wakeup();
        }));

        try {
            String topic = config.getSensorTopic();
            consumer.subscribe(Collections.singletonList(topic));
            log.info("SmartHomeAggregationEngine подписан на топик '{}', ожидаем события от датчиков.", topic);

            while (true) {
                log.debug("Ожидание новых сообщений из Kafka...");
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(config.getFetchMaxWaitMs()));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    log.debug("Получена запись: partition={}, offset={}, value={}", record.partition(), record.offset(), record.value());
                    SensorEventAvro sensorEvent = record.value();

                    snapshotService.updateSnapshot(sensorEvent).ifPresent(snapshot -> {
                        producer.send(snapshot);
                        currentOffsets.put(
                                new TopicPartition(record.topic(), record.partition()),
                                new OffsetAndMetadata(record.offset() + 1)
                        );
                        log.debug("Снапшот для хаба '{}' обновлён и отправлен в топик.", snapshot.getHubId());
                    });
                }

                if (!currentOffsets.isEmpty()) {
                    consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                        if (exception != null) {
                            log.error("Ошибка при асинхронном коммите оффсетов {}", offsets, exception);
                        } else {
                            log.debug("Асинхронный commit оффсетов успешно выполнен: {}", offsets);
                        }
                    });
                }
            }

        } catch (WakeupException ignored) {
            log.info("Consumer пробуждён для завершения работы. Выход из цикла обработки событий.");
        } catch (Exception e) {
            log.error("Неожиданная ошибка во время работы SmartHomeAggregationEngine", e);
        } finally {
            commitOffsetsAndClose();
        }
    }

    /**
     * Выполняет финальный commit оффсетов и закрывает consumer.
     */
    private void commitOffsetsAndClose() {
        try {
            if (!currentOffsets.isEmpty()) {
                log.info("Выполняем финальный commit оффсетов перед завершением работы...");
                consumer.commitSync(currentOffsets);
                log.info("Финальный commit оффсетов успешно выполнен.");
            }
        } catch (Exception e) {
            log.error("Ошибка при финальном коммите оффсетов", e);
        } finally {
            log.info("Закрываем Kafka consumer.");
            consumer.close();
            log.info("Kafka consumer закрыт.");
        }
    }
}