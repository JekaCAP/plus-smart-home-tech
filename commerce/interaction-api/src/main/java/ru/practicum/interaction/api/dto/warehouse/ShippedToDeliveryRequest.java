package ru.practicum.interaction.api.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippedToDeliveryRequest {
    private UUID orderId;

    private UUID deliveryId;
}