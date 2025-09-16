package ru.practicum.shopping.store.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.interaction.api.dto.store.ProductDto;
import ru.practicum.interaction.api.enums.store.ProductCategory;
import ru.practicum.interaction.api.enums.store.QuantityState;
import ru.practicum.interaction.api.feign.contract.StoreContract;
import ru.practicum.shopping.store.service.ProductService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ProductController implements StoreContract {
    private final ProductService productService;

    @Override
    @GetMapping
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category,
                                        Pageable pageable) {
        return productService.getProducts(category, pageable);
    }

    @Override
    @PutMapping
    public ProductDto createProduct(@Valid @RequestBody ProductDto newProductDto) {
        return productService.createNewProduct(newProductDto);
    }

    @Override
    @PostMapping
    public ProductDto updateProduct(@Valid @RequestBody ProductDto updateProductDto) {
        return productService.updateProduct(updateProductDto);
    }

    @Override
    @PostMapping("/removeProductFromStore")
    public Boolean deleteProduct(@RequestBody @NotNull UUID productId) {
        return productService.removeProductFromStore(productId);
    }

    @Override
    @PostMapping("/quantityState")
    public Boolean updateQuantityState(@RequestParam @NotNull UUID productId,
                                       @RequestParam @NotNull QuantityState quantityState) {
        return productService.setProductQuantityState(productId, quantityState);
    }

    @Override
    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable @NotNull UUID productId) {
        return productService.getProductById(productId);
    }

    @Override
    @PostMapping("/products/ids")
    public Map<UUID, ProductDto> findAllByIds(@RequestBody Set<UUID> ids) {
        return ids.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> ProductDto.builder().price(BigDecimal.ZERO).build()
                ));
    }
}