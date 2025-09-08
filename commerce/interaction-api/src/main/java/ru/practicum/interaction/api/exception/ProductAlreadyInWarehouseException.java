package ru.practicum.interaction.api.exception;

public class ProductAlreadyInWarehouseException extends RuntimeException {
    public ProductAlreadyInWarehouseException(String message) {
        super(message);
    }
}
