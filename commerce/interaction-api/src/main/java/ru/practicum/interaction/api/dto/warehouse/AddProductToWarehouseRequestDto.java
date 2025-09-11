package ru.practicum.interaction.api.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddProductToWarehouseRequestDto {

    @NotNull
    private UUID productId;

    @NotNull(message = "Необходимо указать количество")
    @Min(value = 1, message = "Минимальное количество равно 1")
    private Long quantity;
}