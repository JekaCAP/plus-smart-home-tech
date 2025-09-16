package ru.yandex.practicum.payment.service;

import ru.practicum.interaction.api.dto.payment.PaymentDto;
import ru.practicum.interaction.api.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentDto payment(OrderDto orderDto);

    BigDecimal productCost(OrderDto orderDto);

    BigDecimal getTotalCost(OrderDto orderDto);

    void paymentSuccess(UUID paymentId);

    void paymentFailed(UUID paymentId);
}