package ru.practicum.interaction.api.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.interaction.api.exception.BadRequestException;
import ru.practicum.interaction.api.exception.InternalServerErrorException;
import ru.practicum.interaction.api.exception.NoDeliveryFoundException;
import ru.practicum.interaction.api.exception.NoOrderFoundException;
import ru.practicum.interaction.api.exception.NotAuthorizedUserException;
import ru.practicum.interaction.api.exception.PaymentNotFoundException;
import ru.practicum.interaction.api.exception.ProductAlreadyInWarehouseException;
import ru.practicum.interaction.api.exception.ProductNotFoundException;
import ru.practicum.interaction.api.exception.ProductNotInWarehouseException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoDeliveryFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoDeliveryFound(NoDeliveryFoundException ex, HttpServletRequest request) {
        return buildResponse(
                ex,
                HttpStatus.NOT_FOUND,
                "Доставка с указанным идентификатором не найдена.",
                request
        );
    }

    @ExceptionHandler(NoOrderFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoOrderFound(NoOrderFoundException ex, HttpServletRequest request) {
        return buildResponse(
                ex,
                HttpStatus.NOT_FOUND,
                "Заказ с указанным идентификатором не найден.",
                request
        );
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFound(PaymentNotFoundException ex, HttpServletRequest request) {
        return buildResponse(
                ex,
                HttpStatus.NOT_FOUND,
                "Платёж с указанным идентификатором не найден.",
                request
        );
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(InternalServerErrorException ex, HttpServletRequest request) {
        return buildResponse(
                ex,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Внутренняя ошибка сервера. Попробуйте позже.",
                request
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex, HttpServletRequest request) {
        return buildResponse(
                ex,
                HttpStatus.NOT_FOUND,
                "Товар с указанным идентификатором не найден. Проверьте корректность ID.",
                request
        );
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<ErrorResponse> handleNotAuthorized(NotAuthorizedUserException ex, HttpServletRequest request) {
        return buildResponse(
                ex,
                HttpStatus.UNAUTHORIZED,
                "Доступ запрещён. Укажите корректные учетные данные пользователя.",
                request
        );
    }

    @ExceptionHandler(ProductAlreadyInWarehouseException.class)
    public ResponseEntity<ErrorResponse> handleProductAlreadyInWarehouse(ProductAlreadyInWarehouseException ex, HttpServletRequest request) {
        return buildResponse(
                ex,
                HttpStatus.BAD_REQUEST,
                "Попытка добавить товар, который уже существует на складе.",
                request
        );
    }

    @ExceptionHandler(ProductNotInWarehouseException.class)
    public ResponseEntity<ErrorResponse> handleNoProductInWarehouse(ProductNotInWarehouseException ex, HttpServletRequest request) {
        return buildResponse(
                ex,
                HttpStatus.BAD_REQUEST,
                "Информация о товаре отсутствует на складе.",
                request
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return buildResponse(
                ex,
                HttpStatus.BAD_REQUEST,
                "Запрос не может быть обработан. Проверьте переданные данные и попробуйте снова.",
                request
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(Exception ex, HttpStatus status, String userMessage, HttpServletRequest request) {
        log.error(
                "Исключение: {} | HTTP: {} {} | Путь: {} | Сообщение: {}",
                ex.getClass().getSimpleName(),
                status.value(),
                status.getReasonPhrase(),
                request.getRequestURI(),
                ex.getMessage(),
                ex
        );

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(userMessage)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, status);
    }
}