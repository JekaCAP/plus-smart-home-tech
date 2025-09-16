package ru.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.practicum.interaction.api.dto.warehouse.AssemblyProductForOrderFromShoppingCartRequest;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.ShippedToDeliveryRequest;
import ru.practicum.interaction.api.exception.InsufficientWarehouseStockException;
import ru.practicum.warehouse.mapper.WarehouseMapper;
import ru.practicum.warehouse.module.ProductStorage;
import ru.practicum.warehouse.repository.ProductStorageRepository;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import java.util.function.LongUnaryOperator;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseMapper warehouseMapper;
    private final ProductStorageRepository productStorageRepository;

    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};

    @Override
    public void addProductToWarehouse(NewProductInWarehouseRequestDto requestDto) {
        UUID productId = requestDto.getProductId();

        if (productStorageRepository.existsById(productId)) {
            throw new IllegalStateException(
                    String.format("Товар с ID = %s уже заведен на склад", productId));
        }

        ProductStorage product = warehouseMapper.toWarehouse(requestDto);
        productStorageRepository.save(product);
        log.info("Добавлен новый товар на склад: {}", productId);
    }

    @Override
    @Transactional(readOnly = true)
    public BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto cartDto) {
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;

        for (Map.Entry<UUID, Long> entry : cartDto.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            long requestedQty = entry.getValue();

            ProductStorage product = productStorageRepository.getByIdOrThrow(productId);

            if (product.getQuantity() < requestedQty) {
                throw new InsufficientWarehouseStockException(
                        String.format("Недостаточно товара на складе. ID: %s, запрошено: %d, доступно: %d",
                                productId, requestedQty, product.getQuantity()));
            }

            totalWeight += product.getWeight() * requestedQty;
            totalVolume += product.getDimensionDto().getWidth() *
                           product.getDimensionDto().getHeight() *
                           product.getDimensionDto().getDepth() * requestedQty;

            if (product.getFragile()) hasFragile = true;
        }

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile)
                .build();
    }

    @Override
    public void updateProductToWarehouse(AddProductToWarehouseRequestDto requestDto) {
        productStorageRepository.updateQuantityAndSave(
                requestDto.getProductId(),
                (LongUnaryOperator) current -> current + requestDto.getQuantity()
        );
        log.info("Обновлено количество товара на складе: {} (+{})",
                requestDto.getProductId(), requestDto.getQuantity());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        int idx = new SecureRandom().nextInt(ADDRESSES.length);
        String address = ADDRESSES[idx];

        return AddressDto.builder()
                .country(address)
                .city(address)
                .street(address)
                .house(address)
                .flat(address)
                .build();
    }

    @Override
    public void shipOrder(UUID orderId, UUID deliveryId) {
        log.info("Заказ {} отправлен на доставку {}", orderId, deliveryId);
    }

    @Override
    public void returnProducts(UUID orderId, BookedProductsDto bookedProducts) {
        log.info("Обработан возврат заказа {}: товары возвращены на склад", orderId);
    }

    @Override
    public BookedProductsDto assembleOrder(AssemblyProductForOrderFromShoppingCartRequest request) {
        log.info("Собран заказ {}: товары зарезервированы на складе", request.getOrderId());
        return null;
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        log.info("Заказ {} передан в доставку {}", request.getOrderId(), request.getDeliveryId());
    }
}