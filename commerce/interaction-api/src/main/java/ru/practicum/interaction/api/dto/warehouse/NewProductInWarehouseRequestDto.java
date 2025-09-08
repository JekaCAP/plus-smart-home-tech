package ru.practicum.interaction.api.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class NewProductInWarehouseRequestDto {

    @NotNull
    private UUID productId;

    private Boolean fragile;

    @NotNull(message = "Размеры товара обязательны")
    @JsonProperty("dimension")
    private DimensionDto dimensionDto;

    @NotNull(message = "Вес товара обязателен")
    @Min(value = 1, message = "Минимальное значение 1")
    private Double weight;
}