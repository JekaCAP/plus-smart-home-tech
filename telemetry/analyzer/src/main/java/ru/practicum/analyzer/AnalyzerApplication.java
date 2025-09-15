package ru.practicum.analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.practicum.analyzer.config.KafkaConsumerConfig;

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableConfigurationProperties(KafkaConsumerConfig.class)
public class AnalyzerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyzerApplication.class, args);
    }
}
