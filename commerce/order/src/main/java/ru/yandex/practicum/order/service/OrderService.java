package ru.yandex.practicum.order.service;

import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.order.CreateNewOrderRequest;
import ru.practicum.interaction.api.dto.order.ProductReturnRequest;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderDto> getClientOrders(String username);

    OrderDto createNewOrder(CreateNewOrderRequest createNewOrderRequest);

    OrderDto payment(UUID orderId);

    OrderDto paymentFailed(UUID orderId);

    OrderDto delivery(UUID orderId);

    OrderDto deliveryFailed(UUID orderId);

    OrderDto productReturn(ProductReturnRequest productReturnRequest);

    OrderDto completed(UUID orderId);

    OrderDto calculateTotalCost(UUID orderId);

    OrderDto calculateDeliveryCost(UUID orderId);

    OrderDto assembly(UUID orderId);

    OrderDto assemblyFailed(UUID orderId);
}