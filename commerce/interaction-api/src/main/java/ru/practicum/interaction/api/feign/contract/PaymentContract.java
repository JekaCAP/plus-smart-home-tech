package ru.practicum.interaction.api.feign.contract;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "payment-service", path = "/api/v1/payment")
public interface PaymentContract {

    @PostMapping("/productCost")
    BigDecimal productCost(@RequestBody OrderDto orderDto);

    @PostMapping("/totalCost")
    BigDecimal getTotalCost(@RequestBody OrderDto orderDto);

    @PostMapping
    PaymentDto payment(@RequestBody OrderDto orderDto);

    @PostMapping("/{paymentId}/success")
    void paymentSuccess(@RequestParam UUID paymentId);

    @PostMapping("/{paymentId}/failed")
    void paymentFailed(@RequestParam UUID paymentId);
}