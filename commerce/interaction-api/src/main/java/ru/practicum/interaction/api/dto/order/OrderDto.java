package ru.practicum.interaction.api.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;
import ru.practicum.interaction.api.enums.order.OrderState;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {

    @NotNull
    private UUID orderId;

    @NotNull
    private UUID shoppingCartId;

    @NotNull
    private List<OrderProductDto> products;

    @NotNull
    private OrderState status;

    private UUID deliveryId;
    private UUID paymentId;

    private Double totalWeight;
    private Double totalVolume;
    private Boolean fragile;

    private BigDecimal totalPrice;
    private BigDecimal productsPrice;
    private BigDecimal deliveryPrice;
}