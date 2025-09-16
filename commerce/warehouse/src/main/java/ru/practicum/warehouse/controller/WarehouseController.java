package ru.practicum.warehouse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.practicum.interaction.api.dto.warehouse.AssemblyProductForOrderFromShoppingCartRequest;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.ShippedToDeliveryRequest;
import ru.practicum.interaction.api.feign.contract.WarehouseContract;
import ru.practicum.warehouse.service.WarehouseServiceImpl;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseContract {

    private final WarehouseServiceImpl warehouseService;

    @Override
    @PostMapping("/products")
    public void addProduct(@RequestBody @Valid NewProductInWarehouseRequestDto newProduct) {
        warehouseService.addProductToWarehouse(newProduct);
    }

    @Override
    @PostMapping("/products/check")
    public BookedProductsDto checkProductQuantity(@RequestBody @Valid ShoppingCartDto shoppingCart) {
        return warehouseService.checkProductQuantityInWarehouse(shoppingCart);
    }

    @Override
    @PostMapping("/products/update")
    public void updateProduct(@RequestBody @Valid AddProductToWarehouseRequestDto updateRequest) {
        warehouseService.updateProductToWarehouse(updateRequest);
    }

    @Override
    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }

    @Override
    @PostMapping("/orders/{orderId}/ship/{deliveryId}")
    public void shipOrder(@PathVariable UUID orderId, @PathVariable UUID deliveryId) {
        warehouseService.shipOrder(orderId, deliveryId);
    }

    @Override
    @PostMapping("/orders/{orderId}/return")
    public void returnProducts(@PathVariable UUID orderId,
                               @RequestBody @Valid BookedProductsDto bookedProducts) {
        warehouseService.returnProducts(orderId, bookedProducts);
    }

    @Override
    @PostMapping("/orders/assemble")
    public BookedProductsDto assembleOrder(@RequestBody @Valid AssemblyProductForOrderFromShoppingCartRequest request) {
        return warehouseService.assembleOrder(request);
    }

    @Override
    @PostMapping("/orders/shipped")
    public void shippedToDelivery(@RequestBody ShippedToDeliveryRequest request) {
        warehouseService.shippedToDelivery(request);
    }
}