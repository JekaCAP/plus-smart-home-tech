package ru.practicum.shopping.cart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.shopping.cart.module.Cart;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CartMapper {
    ShoppingCartDto toDto(Cart cart);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "username", ignore = true)
    Cart toEntity(ShoppingCartDto dto);
}