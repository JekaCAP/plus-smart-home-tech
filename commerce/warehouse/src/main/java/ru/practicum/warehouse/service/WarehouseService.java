package ru.practicum.warehouse.service;

import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.practicum.interaction.api.dto.warehouse.AssemblyProductForOrderFromShoppingCartRequest;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.ShippedToDeliveryRequest;

import java.util.UUID;

public interface WarehouseService {

    void addProductToWarehouse(NewProductInWarehouseRequestDto requestDto);

    BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto cartDto);

    AddressDto getWarehouseAddress();

    void updateProductToWarehouse(AddProductToWarehouseRequestDto requestDto);

    void shipOrder(UUID orderId, UUID deliveryId);

    void returnProducts(UUID orderId, BookedProductsDto bookedProducts);

    BookedProductsDto assembleOrder(AssemblyProductForOrderFromShoppingCartRequest request);

    void shippedToDelivery(ShippedToDeliveryRequest request);
}