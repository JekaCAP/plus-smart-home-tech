package ru.practicum.aggregator.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

/**
 * Конфигурация Kafka Producer для отправки снапшотов датчиков умного дома.
 * <p>
 * Настраивает основные свойства продюсера, включая адреса брокеров,
 * сериализаторы ключей и значений, и предоставляет бин {@link KafkaProducer}.
 * </p>
 */
@Configuration
@Slf4j
@Getter
public class SmartHomeKafkaProducerConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;

    /**
     * Создаёт бин KafkaProducer для отправки сообщений с типом ключа String
     * и значением {@link SensorsSnapshotAvro}.
     *
     * @return настроенный экземпляр {@link KafkaProducer}
     */
    @Bean
    public KafkaProducer<String, SensorsSnapshotAvro> kafkaProducer() {
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

        log.info("KafkaProducer сконфигурирован и готов к работе. Brokers: {}", bootstrapServers);

        return new KafkaProducer<>(properties);
    }
}