package ru.practicum.analyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Свойства конфигурации для gRPC-клиента HubRouter.
 * <p>
 * Используются для настройки:
 * <ul>
 *     <li>Адрес сервиса {@link #address}</li>
 *     <li>Включение/отключение keep-alive {@link #enableKeepAlive}</li>
 *     <li>Включение keep-alive даже при отсутствии вызовов {@link #keepAliveWithoutCalls}</li>
 *     <li>Тип переговоров (например, "plaintext") {@link #negotiationType}</li>
 * </ul>
 * <p>
 * Значения подставляются из настроек Spring Boot по префиксу {@code grpc.client.hub-router}.
 */
@Data
@ConfigurationProperties(prefix = "grpc.client.hub-router")
public class GrpcClientProperties {
    private String address;
    private boolean enableKeepAlive;
    private boolean keepAliveWithoutCalls;
    private String negotiationType;
}