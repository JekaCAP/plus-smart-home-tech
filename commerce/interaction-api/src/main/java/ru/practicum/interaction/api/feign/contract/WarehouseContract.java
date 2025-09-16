package ru.practicum.interaction.api.feign.contract;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.practicum.interaction.api.dto.warehouse.AssemblyProductForOrderFromShoppingCartRequest;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.ShippedToDeliveryRequest;

import java.util.UUID;

@FeignClient(name = "warehouse-service", path = "/api/v1/warehouse")
public interface WarehouseContract {

    @PostMapping("/products")
    void addProduct(@RequestBody @Valid NewProductInWarehouseRequestDto newProduct);

    @PostMapping("/products/check")
    BookedProductsDto checkProductQuantity(@RequestBody @Valid ShoppingCartDto shoppingCart);

    @PostMapping("/products/update")
    void updateProduct(@RequestBody @Valid AddProductToWarehouseRequestDto updateRequest);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();

    @PostMapping("/orders/{orderId}/ship/{deliveryId}")
    void shipOrder(@PathVariable UUID orderId, @PathVariable UUID deliveryId);

    @PostMapping("/orders/{orderId}/return")
    void returnProducts(@PathVariable UUID orderId, @RequestBody @Valid BookedProductsDto bookedProducts);

    @PostMapping("/orders/assemble")
    BookedProductsDto assembleOrder(@RequestBody @Valid AssemblyProductForOrderFromShoppingCartRequest request);

    @PostMapping("shipped")
    void shippedToDelivery(@RequestBody ShippedToDeliveryRequest request);
}