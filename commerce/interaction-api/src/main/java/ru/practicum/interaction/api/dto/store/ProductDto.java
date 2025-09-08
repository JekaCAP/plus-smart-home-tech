package ru.practicum.interaction.api.dto.store;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import ru.practicum.interaction.api.enums.store.ProductCategory;
import ru.practicum.interaction.api.enums.store.ProductState;
import ru.practicum.interaction.api.enums.store.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    private UUID productId;

    @NotBlank(message = "Наименование товара не может быть пустым")
    @Size(max = 255, message = "Наименование товара должно быть до 255 символов")
    private String productName;

    @NotBlank(message = "Описание товара не может быть пустым")
    @Size(max = 255, message = "Описание товара должно быть до 255 символов")
    private String description;

    @Size(max = 255, message = "Ссылка на картинку должна быть до 255 символов")
    private String imageSrc;

    @NotNull(message = "Необходимо указать остаток товара")
    private QuantityState quantityState;

    @NotNull(message = "Необходимо указать статус товара")
    private ProductState productState;

    @NotNull(message = "Необходимо указать категорию товара")
    private ProductCategory productCategory;

    @NotNull(message = "Необходимо указать цену товара")
    @DecimalMin(value = "1.00", message = "Минимальная стоимость товара 1 руб 00 коп")
    private BigDecimal price;
}