package ru.practicum.interaction.api.feign.contract;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.delivery.DeliveryDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "delivery-service", path = "/api/v1/delivery")
public interface DeliveryContract {

    @PutMapping
    DeliveryDto planDelivery(@RequestBody DeliveryDto deliveryDto);

    @PostMapping("/cost")
    BigDecimal deliveryCost(@RequestBody OrderDto orderDto);

    @PostMapping("/{deliveryId}/success")
    void deliverySuccess(@PathVariable UUID deliveryId);

    @PostMapping("/{deliveryId}/failed")
    void deliveryFailed(@PathVariable UUID deliveryId);

    @PostMapping("/{orderId}/shipped")
    void shippedToDelivery(@PathVariable UUID orderId, @RequestParam UUID deliveryId);
}