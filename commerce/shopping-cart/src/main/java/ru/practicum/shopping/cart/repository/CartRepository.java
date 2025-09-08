package ru.practicum.shopping.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.interaction.api.enums.cart.CartState;
import ru.practicum.interaction.api.exception.NotFoundException;
import ru.practicum.shopping.cart.module.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUsernameAndStatus(String username, CartState status);

    Optional<Cart> findByUsername(String username);

    default Cart getByUsernameAndStatusOrThrow(String username, CartState status) {
        return findByUsernameAndStatus(username, status)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Активной корзины покупок для пользователя %s не найдено", username)
                ));
    }

    default Cart getByUsernameOrThrow(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Корзина для пользователя %s не найдена", username)));
    }
}