package ru.practicum.shopping.cart.service;

import ru.practicum.interaction.api.dto.cart.ChangeProductQuantityRequestDto;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface CartService {
    ShoppingCartDto getShoppingCart(String username);

    ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> products);

    void deactivateCurrentShoppingCart(String username);

    ShoppingCartDto removeFromShoppingCart(String username, Set<UUID> request);

    ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequestDto requestDto);
}