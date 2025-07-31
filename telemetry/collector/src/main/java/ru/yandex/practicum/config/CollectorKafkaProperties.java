package ru.yandex.practicum.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "collector.kafka.producer")
public class CollectorKafkaProperties {
    private Map<String, String> properties;
    private Topics topics;

    @Data
    public static class Topics {
        private String sensorsEvents;
        private String hubsEvents;
    }
}