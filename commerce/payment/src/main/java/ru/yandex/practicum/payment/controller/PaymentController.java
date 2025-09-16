package ru.yandex.practicum.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.payment.PaymentDto;
import ru.yandex.practicum.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    PaymentDto payment(@RequestBody OrderDto orderDto) {
        return paymentService.payment(orderDto);
    }

    @PostMapping("/totalCost")
    BigDecimal getTotalCost(@RequestBody OrderDto orderDto) {
        return paymentService.getTotalCost(orderDto);
    }

    @PostMapping("/success")
    void paymentSuccess(@RequestParam UUID paymentId) {
        paymentService.paymentSuccess(paymentId);
    }

    @PostMapping("/productCost")
    BigDecimal productCost(@RequestBody OrderDto orderDto) {
        return paymentService.productCost(orderDto);
    }

    @PostMapping("failed")
    void paymentFailed(@RequestParam UUID paymentId) {
        paymentService.paymentFailed(paymentId);
    }
}