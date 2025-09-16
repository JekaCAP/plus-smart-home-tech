package ru.practicum.interaction.api.feign.contract;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.interaction.api.dto.store.ProductDto;
import ru.practicum.interaction.api.enums.store.ProductCategory;
import ru.practicum.interaction.api.enums.store.QuantityState;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = "shopping-store-service", path = "/api/v1/store")
public interface StoreContract {

    @GetMapping
    Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable);

    @PostMapping
    ProductDto createProduct(@RequestBody @Valid ProductDto newProductDto);

    @PutMapping
    ProductDto updateProduct(@RequestBody @Valid ProductDto updateProductDto);

    @DeleteMapping
    Boolean deleteProduct(@RequestBody @NotNull UUID productId);

    @PutMapping("/quantity")
    Boolean updateQuantityState(@RequestParam @NotNull UUID productId,
                                @RequestParam @NotNull QuantityState quantityState);

    @GetMapping("/{productId}")
    ProductDto getProductById(@PathVariable @NotNull UUID productId);

    @PostMapping("/products/ids")
    Map<UUID, ProductDto> findAllByIds(@RequestBody Set<UUID> ids);
}