package ru.practicum.interaction.api.dto.order;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderProductDto {

    @NotNull
    private UUID productId;

    @NotNull
    private Long quantity;

    @NotNull
    private BigDecimal price;
}