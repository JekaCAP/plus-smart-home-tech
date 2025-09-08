package ru.practicum.interaction.api.dto.cart;

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
public class ChangeProductQuantityRequestDto {

    @NotNull
    private UUID productId;

    @NotNull(message = "Количество необходимо указать")
    @Min(value = 0, message = "Количество должно быть не отрицательное")
    private Long newQuantity;
}