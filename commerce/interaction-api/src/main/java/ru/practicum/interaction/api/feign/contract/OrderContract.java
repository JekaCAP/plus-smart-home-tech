package ru.practicum.interaction.api.feign.contract;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.interaction.api.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "order-service", path = "/api/v1/orders")
public interface OrderContract {

    @PostMapping
    OrderDto createOrder(@RequestParam UUID shoppingCartId);

    @PostMapping("/{orderId}/pay")
    OrderDto payOrder(@PathVariable UUID orderId);

    @GetMapping("/{orderId}/total-cost")
    BigDecimal calculateTotalCost(@PathVariable UUID orderId);

    @GetMapping("/{orderId}/delivery-cost")
    BigDecimal calculateDeliveryCost(@PathVariable UUID orderId);

    @GetMapping
    List<OrderDto> getOrders(@RequestParam String username);

    @PostMapping("/{orderId}/assemble")
    void assembleOrder(@PathVariable UUID orderId);

    @PostMapping("/{orderId}/ship")
    void shipOrder(@PathVariable UUID orderId, @RequestParam UUID deliveryId);

    @PostMapping("/{orderId}/return")
    void returnOrder(@PathVariable UUID orderId);

    @PostMapping("/{orderId}/fail-payment")
    void failPayment(@PathVariable UUID orderId);

    @PostMapping("/{orderId}/fail-delivery")
    void failDelivery(@PathVariable UUID orderId);

    @PostMapping("/{orderId}/fail-assembly")
    void failAssembly(@PathVariable UUID orderId);

    @PostMapping("/{orderId}/complete")
    OrderDto completeOrder(@PathVariable UUID orderId);

    @PostMapping("/{orderId}/delivered")
    OrderDto delivery(@PathVariable UUID orderId);
}