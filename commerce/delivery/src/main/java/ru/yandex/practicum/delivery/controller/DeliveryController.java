package ru.yandex.practicum.delivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.delivery.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto planDelivery(@RequestBody DeliveryDto deliveryDto) {
        return deliveryService.planDelivery(deliveryDto);
    }

    @PostMapping("/picked")
    public void deliveryPicked(@RequestParam UUID deliveryId) {
        deliveryService.deliveryPicked(deliveryId);
    }

    @PostMapping("/failed")
    public void deliveryFailed(@RequestParam UUID orderId) {
        deliveryService.deliveryFailed(orderId);
    }

    @PostMapping("/successful")
    public void deliverySuccessful(@RequestParam UUID orderId) {
        deliveryService.deliverySuccessful(orderId);
    }

    @PostMapping("/cost")
    public BigDecimal deliveryCost(@RequestBody OrderDto orderDto) {
        return deliveryService.deliveryCost(orderDto);
    }
}