package ru.practicum.interaction.api.dto.order;


import jakarta.validation.constraints.NotNull;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.interaction.api.dto.warehouse.AddressDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNewOrderRequest {

    @NotNull
    private ShoppingCartDto shoppingCart;

    @NotNull
    private AddressDto address;

    @NotNull
    private String username;
}