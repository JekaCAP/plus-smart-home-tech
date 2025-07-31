package ru.yandex.practicum.exception;

public class SensorHandlerNotFound extends RuntimeException {
    public SensorHandlerNotFound(String message) {
        super(message);
    }
}
