package ru.practicum.analyzer.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация Kafka consumer для приложения.
 * <p>
 * Настраивает фабрики потребителей и контейнеры для двух типов сообщений:
 * <ul>
 *     <li>{@link HubEventAvro} — события хаба</li>
 *     <li>{@link SensorsSnapshotAvro} — снимки состояния сенсоров</li>
 * </ul>
 * <p>
 * Использует параметры из application.yml/properties через {@link ConfigurationProperties} с префиксом
 * {@code spring.kafka}.
 * <p>
 * Класс обеспечивает:
 * <ul>
 *     <li>Создание {@link ConsumerFactory} для Hub и Snapshot</li>
 *     <li>Создание {@link ConcurrentKafkaListenerContainerFactory} для Hub и Snapshot</li>
 *     <li>Логирование конфигурации при инициализации</li>
 * </ul>
 */
@Configuration
@EnableKafka
@ConfigurationProperties("spring.kafka")
@Data
@Slf4j
public class KafkaConsumerConfig {

    private Hub hub = new Hub();
    private Snapshot snapshot = new Snapshot();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Hub {
        private String bootstrapServers;
        private String groupId;
        private String clientId;
        private String consumerTopics;
        private String valueDeserializer;
        private String keyDeserializer;
        private boolean enableAutoCommit;
        private int autoCommitInterval;
        private String autoOffsetReset;
        private int maxPollRecords;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Snapshot {
        private String bootstrapServers;
        private String groupId;
        private String clientId;
        private String consumerTopics;
        private String valueDeserializer;
        private String keyDeserializer;
        private boolean enableAutoCommit;
        private String autoOffsetReset;
        private int maxPollRecords;
        private int maxPollInterval;
        private String listenerAckMode;
        private String isolationLevel;
    }

    @Bean
    public ConsumerFactory<String, HubEventAvro> hubConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hub.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, hub.getGroupId());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, hub.getClientId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, hub.getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, hub.getValueDeserializer());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, hub.isEnableAutoCommit());
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, hub.getAutoCommitInterval());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, hub.getAutoOffsetReset());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, hub.getMaxPollRecords());

        log.debug("Создание hubConsumerFactory с конфигурацией: {}", props);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = "hubKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, HubEventAvro> hubKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, HubEventAvro> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(hubConsumerFactory());

        log.debug("Создание hubKafkaListenerContainerFactory для чтения сообщений на темы: {}", hub.getConsumerTopics());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, SensorsSnapshotAvro> snapshotConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, snapshot.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, snapshot.getGroupId());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, snapshot.getClientId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, snapshot.getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, snapshot.getValueDeserializer());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, snapshot.isEnableAutoCommit());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, snapshot.getAutoOffsetReset());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, snapshot.getMaxPollRecords());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, snapshot.getMaxPollInterval());
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, snapshot.getIsolationLevel());

        log.debug("Создание snapshotConsumerFactory с конфигурацией: {}", props);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = "snapshotKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, SensorsSnapshotAvro> snapshotKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SensorsSnapshotAvro> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(snapshotConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        log.debug("Создание snapshotKafkaListenerContainerFactory для чтения сообщений на темы: {}", snapshot.getConsumerTopics());

        return factory;
    }
}