package ru.practicum.aggregator.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.Properties;

/**
 * Конфигурация Kafka Consumer для чтения событий от датчиков умного дома.
 * <p>
 * Настраивает свойства консьюмера: группа, десериализаторы, управление оффсетами,
 * максимальное количество сообщений за один poll, минимальный размер fetch и т.д.
 * Предоставляет бин {@link KafkaConsumer}, готовый к подписке на топики.
 * </p>
 */
@Configuration
@Slf4j
@Getter
public class SmartHomeKafkaConsumerConfig {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.key-deserializer}")
    private String keyDeserializer;

    @Value("${spring.kafka.consumer.value-deserializer}")
    private String valueDeserializer;

    @Value("${aggregator.kafka.topic.sensors}")
    private String sensorTopic;

    @Value("${spring.kafka.consumer.group-id}")
    private String clientGroupId;

    @Value("${spring.kafka.consumer.properties.max.poll.records}")
    private int maxPollRecords;

    @Value("${spring.kafka.consumer.properties.fetch.min.bytes}")
    private int fetchMinBytes;

    @Value("${spring.kafka.consumer.properties.enable.auto.commit}")
    private boolean enableAutoCommit;

    @Value("${spring.kafka.consumer.properties.fetch.max.wait.ms}")
    private int fetchMaxWaitMs;

    @Value("${spring.kafka.consumer.properties.max.partition.fetch.bytes}")
    private int maxPartitionFetchBytes;

    /**
     * Создаёт бин KafkaConsumer для чтения событий с типом ключа String
     * и значением {@link SensorEventAvro}.
     *
     * @return настроенный экземпляр {@link KafkaConsumer}
     */
    @Bean
    public KafkaConsumer<String, SensorEventAvro> kafkaConsumer() {
        Properties properties = new Properties();

        properties.put(ConsumerConfig.GROUP_ID_CONFIG, clientGroupId);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        properties.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinBytes);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, fetchMaxWaitMs);
        properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);

        log.info("KafkaConsumer сконфигурирован и готов к подписке на топик '{}', brokers: {}",
                sensorTopic, bootstrapServers);

        return new KafkaConsumer<>(properties);
    }
}