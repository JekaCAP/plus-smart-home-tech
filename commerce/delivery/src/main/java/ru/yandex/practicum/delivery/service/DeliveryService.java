package ru.yandex.practicum.delivery.service;

import ru.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.practicum.interaction.api.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto planDelivery(DeliveryDto deliveryDto);

    BigDecimal deliveryCost(OrderDto orderDto);

    void deliverySuccessful(UUID orderId);

    void deliveryFailed(UUID orderId);

    void deliveryPicked(UUID orderId);
}