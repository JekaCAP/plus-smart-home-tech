package ru.yandex.practicum.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${grpc.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${grpc.kafka.producer.key-serializer}")
    private String keySerializer;

    @Value("${grpc.kafka.producer.value-serializer}")
    private String valueSerializer;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

        configProps.putAll(collectorProps.getProperties());

        log.info("Initializing Kafka ProducerFactory with the following configuration: {}", configProps);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        log.info("Creating KafkaTemplate with target topics: [sensors: '{}', hubs: '{}']",
                collectorProps.getTopics().getSensorsEvents(),
                collectorProps.getTopics().getHubsEvents());

        return new KafkaTemplate<>(producerFactory());
    }

    public String getHubTopic() {
        return collectorProps.getTopics().getHubsEvents();
    }

    public String getSensorTopic() {
        return collectorProps.getTopics().getSensorsEvents();
    }
}