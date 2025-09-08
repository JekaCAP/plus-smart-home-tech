package ru.practicum.interaction.api.dto.warehouse;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class DimensionDto {

    @NotNull(message = "Необходимо указать ширину")
    @Min(value = 1, message = "минимальное значение 1")
    private Double width;

    @NotNull(message = "Необходимо указать высоту")
    @Min(value = 1, message = "минимальное значение 1")
    private Double height;

    @NotNull(message = "Необходимо указать глубину")
    @Min(value = 1, message = "минимальное значение 1")
    private Double depth;
}