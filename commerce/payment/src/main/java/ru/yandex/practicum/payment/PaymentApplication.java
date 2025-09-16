package ru.yandex.practicum.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.interaction.api.util.ErrorDecoderConfig;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(defaultConfiguration = ErrorDecoderConfig.class, basePackages = "ru.practicum.interaction.api.feign.contract")
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}