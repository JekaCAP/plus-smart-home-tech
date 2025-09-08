package ru.practicum.interaction.api.exception;

public class ProductNotInWarehouseException extends RuntimeException {
    public ProductNotInWarehouseException(String message) {
        super(message);
    }
}
