package ru.yandex.practicum.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.interaction.api.util.ErrorDecoderConfig;


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(
        basePackages = "ru.practicum.interaction.api.feign.contract",
        defaultConfiguration = ErrorDecoderConfig.class
)
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}