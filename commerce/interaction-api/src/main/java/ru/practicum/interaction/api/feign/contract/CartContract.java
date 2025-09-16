package ru.practicum.interaction.api.feign.contract;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.interaction.api.dto.cart.ChangeProductQuantityRequestDto;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = "cart-service", path = "/api/v1/cart")
public interface CartContract {

    @GetMapping
    ShoppingCartDto getCart(@RequestParam String username);

    @PostMapping("/products")
    ShoppingCartDto addProduct(@RequestParam String username,
                               @RequestBody @NotNull Map<UUID, Long> products);

    @DeleteMapping("/products")
    ShoppingCartDto deleteProduct(@RequestParam String username,
                                  @RequestBody Set<UUID> products);

    @PutMapping("/products/quantity")
    ShoppingCartDto updateProductQuantity(@RequestParam String username,
                                          @RequestBody @Valid ChangeProductQuantityRequestDto request);

    @PostMapping("/deactivate")
    void deactivateCart(@RequestParam String username);
}