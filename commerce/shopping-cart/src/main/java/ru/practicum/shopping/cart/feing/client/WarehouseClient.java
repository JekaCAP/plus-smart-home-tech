package ru.practicum.shopping.cart.feing.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.practicum.interaction.api.feign.contract.WarehouseContract;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient extends WarehouseContract {

    @PostMapping("/products")
    void addNewProductToWarehouse(@RequestBody @Valid NewProductInWarehouseRequestDto newProductInWarehouseRequestDto);

    @PostMapping("/products/check")
    BookedProductsDto checkProductQuantityInWarehouse(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @PostMapping("/products/update")
    void updateProductToWarehouse(@RequestBody AddProductToWarehouseRequestDto addProductToWarehouseRequestDto);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();
}