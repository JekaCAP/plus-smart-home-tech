package ru.yandex.practicum.order.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.interaction.api.dto.order.OrderProductDto;
import ru.yandex.practicum.order.model.Order;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.order.CreateNewOrderRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OrderMapper {

    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "deliveryId", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "products", source = "shoppingCart.products")
    Order toEntity(CreateNewOrderRequest request);

    @Mapping(target = "products", expression = "java(mapProducts(order.getProducts()))")
    OrderDto toOrderDto(Order order);

    default List<OrderProductDto> mapProducts(Map<UUID, Integer> products) {
        if (products == null) return Collections.emptyList();
        return products.entrySet().stream()
                .map(e -> new OrderProductDto())
                .collect(Collectors.toList());
    }
}