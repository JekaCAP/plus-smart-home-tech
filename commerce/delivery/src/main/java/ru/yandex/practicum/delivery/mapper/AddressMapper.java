package ru.yandex.practicum.delivery.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.yandex.practicum.delivery.model.Address;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AddressMapper {

    AddressDto toAddressDto(Address address);

    Address fromAddressDto(AddressDto addressDto);
}