package ru.yandex.practicum.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class TelemetryKafkaProducerConfig {

    private final CollectorKafkaProperties collectorProps;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                collectorProps.getProperties().getOrDefault("bootstrap-servers", "localhost:9092"));
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                collectorProps.getProperties().getOrDefault("key-serializer", "org.apache.kafka.common.serialization.StringSerializer"));
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                collectorProps.getProperties().getOrDefault("value-serializer", "serializer.GeneralAvroSerializer"));

        configProps.putAll(collectorProps.getProperties());

        log.info("Initializing Kafka ProducerFactory with configuration: {}", configProps);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        log.info("Creating KafkaTemplate for topics: sensors='{}', hubs='{}'",
                collectorProps.getTopics().getSensorsEvents(),
                collectorProps.getTopics().getHubsEvents());

        return new KafkaTemplate<>(producerFactory());
    }

    public String getSensorTopic() {
        return collectorProps.getTopics().getSensorsEvents();
    }

    public String getHubTopic() {
        return collectorProps.getTopics().getHubsEvents();
    }
}