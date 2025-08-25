package ru.practicum.analyzer.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

/**
 * Конфигурация gRPC-клиента для работы с HubRouter.
 * <p>
 * Настраивает:
 * <ul>
 *     <li>gRPC-канал {@link ManagedChannel} к HubRouter с поддержкой plaintext или keep-alive опций.</li>
 *     <li>gRPC blocking stub {@link HubRouterControllerGrpc.HubRouterControllerBlockingStub} для вызовов RPC.</li>
 * </ul>
 * <p>
 * Использует параметры из {@link GrpcClientProperties} для адреса, типа переговоров и keep-alive.
 * Канал автоматически закрывается при завершении работы Spring (destroyMethod="shutdown").
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(GrpcClientProperties.class)
@RequiredArgsConstructor
public class HubGrpcConfig {

    private final GrpcClientProperties props;

    @Bean(destroyMethod = "shutdown")
    public ManagedChannel hubRouterChannel() {
        ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forTarget(props.getAddress());

        if (isPlaintext()) builder.usePlaintext();
        if (props.isEnableKeepAlive()) {
            builder.keepAliveWithoutCalls(props.isKeepAliveWithoutCalls());
        }

        ManagedChannel channel = builder.build();
        logChannelInit(channel);
        return channel;
    }

    @Bean
    public HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub(
            ManagedChannel hubRouterChannel) {
        return HubRouterControllerGrpc.newBlockingStub(hubRouterChannel);
    }

    private boolean isPlaintext() {
        return "plaintext".equalsIgnoreCase(props.getNegotiationType());
    }

    private void logChannelInit(ManagedChannel channel) {
        log.info(
                "gRPC hub-router channel init: address={}, plaintext={}, enableKeepAlive={}, keepAliveWithoutCalls={}",
                props.getAddress(),
                isPlaintext(),
                props.isEnableKeepAlive(),
                props.isKeepAliveWithoutCalls()
        );
    }
}