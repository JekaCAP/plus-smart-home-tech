package ru.practicum.shopping.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.interaction.api.enums.store.ProductCategory;
import ru.practicum.interaction.api.exception.ProductNotFoundException;
import ru.practicum.shopping.store.module.Product;

import java.util.UUID;
import java.util.function.Consumer;

public interface StoreRepository extends JpaRepository<Product, UUID> {

    Page<Product> findAllByProductCategory(ProductCategory category, Pageable pageable);

    default Product getByIdOrThrow(UUID productId) {
        return findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Товар c id = %s не найден", productId)
                ));
    }

    default Product updateAndSave(UUID productId, Consumer<Product> updater) {
        Product product = getByIdOrThrow(productId);
        updater.accept(product);
        return save(product);
    }
}