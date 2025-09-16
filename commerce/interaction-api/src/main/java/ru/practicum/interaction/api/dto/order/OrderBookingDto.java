package ru.practicum.interaction.api.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderBookingDto {

    @NotNull
    private UUID productId;

    @NotNull
    private Long quantity;
}