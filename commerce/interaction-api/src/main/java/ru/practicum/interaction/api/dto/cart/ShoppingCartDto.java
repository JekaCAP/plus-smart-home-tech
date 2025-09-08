package ru.practicum.interaction.api.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingCartDto {

    @NotNull
    private UUID shoppingCartId;

    @NotNull(message = "Необходимо указать идентификатор товара и его количество")
    private Map<UUID, Long> products;
}