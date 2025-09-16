package ru.yandex.practicum.delivery.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.delivery.model.Delivery;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = AddressMapper.class
)
public interface DeliveryMapper {

    DeliveryDto toDeliveryDto(Delivery delivery);

    Delivery fromDeliveryDto(DeliveryDto deliveryDto);
}