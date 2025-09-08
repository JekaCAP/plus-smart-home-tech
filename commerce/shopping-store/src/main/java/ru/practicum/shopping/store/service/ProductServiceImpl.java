package ru.practicum.shopping.store.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.api.dto.store.ProductDto;
import ru.practicum.interaction.api.enums.store.ProductCategory;
import ru.practicum.interaction.api.enums.store.ProductState;
import ru.practicum.interaction.api.enums.store.QuantityState;
import ru.practicum.shopping.store.mapper.ProductMapper;
import ru.practicum.shopping.store.module.Product;
import ru.practicum.shopping.store.repository.StoreRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final StoreRepository storeRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        return productMapper.toDtoPage(storeRepository.findAllByProductCategory(category, pageable));
    }

    @Override
    public ProductDto createNewProduct(@Valid ProductDto newProductDto) {
        Product product = productMapper.toEntity(newProductDto);
        product = storeRepository.save(product);
        log.info("Создан новый товар с ID {}", product.getProductId());
        return productMapper.toDto(product);
    }

    @Override
    public ProductDto updateProduct(@Valid ProductDto updateProductDto) {
        Product product = storeRepository.updateAndSave(updateProductDto.getProductId(),
                p -> productMapper.updateFromDto(updateProductDto, p));
        log.info("Обновлен товар с ID {}", product.getProductId());
        return productMapper.toDto(product);
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        storeRepository.updateAndSave(productId, p -> p.setProductState(ProductState.DEACTIVATE));
        log.info("Товар с ID {} деактивирован", productId);
        return true;
    }

    @Override
    public boolean setProductQuantityState(UUID productId, QuantityState quantityState) {
        storeRepository.updateAndSave(productId, p -> p.setQuantityState(quantityState));
        log.info("Обновлено количество товара с ID {}: {}", productId, quantityState);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID productId) {
        return productMapper.toDto(storeRepository.getByIdOrThrow(productId));
    }
}