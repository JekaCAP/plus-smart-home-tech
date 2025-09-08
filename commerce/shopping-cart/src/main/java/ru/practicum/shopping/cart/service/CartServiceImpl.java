package ru.practicum.shopping.cart.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.api.dto.cart.ChangeProductQuantityRequestDto;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.api.enums.cart.CartState;
import ru.practicum.interaction.api.exception.BadRequestException;
import ru.practicum.interaction.api.exception.NotAuthorizedUserException;
import ru.practicum.interaction.api.exception.NotFoundException;
import ru.practicum.shopping.cart.feing.client.WarehouseClient;
import ru.practicum.shopping.cart.mapper.CartMapper;
import ru.practicum.shopping.cart.module.Cart;
import ru.practicum.shopping.cart.repository.CartRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final WarehouseClient warehouseClient;

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getShoppingCart(String username) {
        Cart cart = getOrCreateCart(username);
        return cartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> products) {
        checkUsername(username);
        validateProductsNotEmpty(products);

        Cart cart = getOrCreateCart(username);
        cartRepository.save(cart);

        mergeProducts(cart.getProducts(), products);

        checkWarehouseAvailability(cart);

        cart = cartRepository.save(cart);

        ShoppingCartDto result = cartMapper.toDto(cart);
        log.debug("Товары добавлены в корзину пользователя {}. Итоговое количество товаров: {}",
                username, result.getProducts().size());
        return result;
    }

    @Override
    public void deactivateCurrentShoppingCart(String username) {
        Cart cart = cartRepository.getByUsernameOrThrow(username);

        if (cart.getStatus() != CartState.DEACTIVATE) {
            cart.setStatus(CartState.DEACTIVATE);
            cartRepository.save(cart);
            log.info("Корзина пользователя {} деактивирована", username);
        } else {
            log.debug("Корзина уже деактивирована для пользователя: {}", username);
        }
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, Set<UUID> productIds) {
        checkUsername(username);
        if (productIds == null || productIds.isEmpty()) {
            throw new BadRequestException("Список товаров для удаления не может быть пустым или null");
        }

        Cart cart = cartRepository.getByUsernameAndStatusOrThrow(username, CartState.ACTIVE);

        if (cart.getProducts() != null) {
            cart.getProducts().keySet().removeAll(productIds);
        }

        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequestDto requestDto) {
        checkUsername(username);
        validateRequestDto(requestDto);

        Cart cart = cartRepository.getByUsernameAndStatusOrThrow(username, CartState.ACTIVE);

        Map<UUID, Long> products = cart.getProducts();
        if (products == null) {
            products = new HashMap<>();
            cart.setProducts(products);
        }

        UUID productId = requestDto.getProductId();
        Long newQuantity = requestDto.getNewQuantity();

        if (!products.containsKey(productId)) {
            throw new BadRequestException(String.format("Товар с ID %s отсутствует в корзине", productId));
        }

        if (newQuantity == 0) {
            products.remove(productId);
        } else {
            products.put(productId, newQuantity);
        }

        return cartMapper.toDto(cartRepository.save(cart));
    }

    private Cart getOrCreateCart(String username) {
        checkUsername(username);

        return cartRepository.findByUsername(username)
                .orElseGet(() -> {
                    log.debug("Корзина для пользователя {} не найдена. Создаю новую.", username);
                    return Cart.builder()
                            .username(username)
                            .status(CartState.ACTIVE)
                            .products(new HashMap<>())
                            .build();
                });
    }

    private void mergeProducts(Map<UUID, Long> existingProducts, Map<UUID, Long> newProducts) {
        if (existingProducts == null) {
            throw new IllegalStateException("Существующие продукты не могут быть null");
        }
        newProducts.forEach((productId, quantity) ->
                existingProducts.merge(productId, quantity, Long::sum));
    }

    private void checkWarehouseAvailability(Cart cart) {
        try {
            log.info("Проверка наличия товаров на складе: id={}, products={}",
                    cart.getShoppingCartId(), cart.getProducts());

            BookedProductsDto bookedProducts = warehouseClient.checkProductQuantityInWarehouse(cartMapper.toDto(cart));

            log.info("Проверено наличие на складе: {}", bookedProducts);
        } catch (FeignException e) {
            log.error("Ошибка вызова склада: {}", e.getMessage());
            throw new RuntimeException("Склад недоступен", e);
        }
    }

    private void checkUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым.");
        }
    }

    private void validateProductsNotEmpty(Map<UUID, Long> products) {
        if (products == null || products.isEmpty()) {
            throw new BadRequestException("Список продуктов не может быть пустым");
        }
    }

    private void validateRequestDto(ChangeProductQuantityRequestDto requestDto) {
        if (requestDto == null) {
            throw new BadRequestException("Запрос на обновление не может быть null");
        }
    }
}