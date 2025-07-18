package ru.yandex.practicum.exception;

public class HubHandlerNotFound extends RuntimeException {
    public HubHandlerNotFound(String message) {
        super(message);
    }
}
