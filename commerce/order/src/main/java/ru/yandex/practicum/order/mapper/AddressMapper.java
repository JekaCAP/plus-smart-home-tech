package ru.yandex.practicum.order.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.yandex.practicum.order.model.Address;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AddressMapper {

    @Mapping(target = "addressId", ignore = true)
    Address fromAddressDto(AddressDto addressDto);

    AddressDto toAddressDto(Address address);
}