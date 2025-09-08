package ru.practicum.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.interaction.api.exception.ProductNotInWarehouseException;
import ru.practicum.warehouse.module.ProductStorage;

import java.util.UUID;
import java.util.function.LongUnaryOperator;

public interface ProductStorageRepository extends JpaRepository<ProductStorage, UUID> {

    default ProductStorage getByIdOrThrow(UUID productId) {
        return findById(productId)
                .orElseThrow(() -> new ProductNotInWarehouseException(
                        String.format("Товар с ID = %s не найден на складе", productId)
                ));
    }

    default ProductStorage updateQuantityAndSave(UUID productId, long deltaQuantity) {
        ProductStorage product = getByIdOrThrow(productId);
        product.setQuantity(product.getQuantity() + deltaQuantity);
        return save(product);
    }

    default ProductStorage updateQuantityAndSave(UUID productId, LongUnaryOperator updater) {
        ProductStorage product = getByIdOrThrow(productId);
        product.setQuantity(updater.applyAsLong(product.getQuantity()));
        return save(product);
    }
}