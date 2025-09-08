package ru.practicum.interaction.api.exception;

public class InsufficientWarehouseStockException extends RuntimeException {
  public InsufficientWarehouseStockException(String message) {
    super(message);
  }
}
