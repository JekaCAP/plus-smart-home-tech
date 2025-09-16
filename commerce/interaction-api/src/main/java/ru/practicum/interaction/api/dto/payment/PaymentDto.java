package ru.practicum.interaction.api.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import ru.practicum.interaction.api.enums.payment.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {

    @NotNull
    private UUID paymentId;

    @NotNull
    private UUID orderId;

    @NotNull
    private BigDecimal productsPrice;

    @NotNull
    private BigDecimal deliveryPrice;

    @NotNull
    private BigDecimal totalPrice;

    @NotNull
    private PaymentState status;
}