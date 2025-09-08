package ru.practicum.shopping.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.interaction.api.dto.store.ProductDto;
import ru.practicum.shopping.store.module.Product;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProductMapper {
    Product toEntity(ProductDto dto);

    ProductDto toDto(Product entity);

    List<ProductDto> toDtoList(List<Product> entities);

    List<Product> toEntityList(List<ProductDto> dtos);

    default Page<ProductDto> toDtoPage(Page<Product> entityPage) {
        List<ProductDto> dtos = entityPage.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, entityPage.getPageable(), entityPage.getTotalElements());
    }

    default Page<Product> toEntityPage(Page<ProductDto> dtoPage) {
        List<Product> entities = dtoPage.getContent().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(entities, dtoPage.getPageable(), dtoPage.getTotalElements());
    }

    @Mapping(target = "productId", ignore = true)
    void updateFromDto(ProductDto dto, @MappingTarget Product entity);
}