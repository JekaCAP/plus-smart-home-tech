package ru.yandex.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.handlers.hub.GrpcHubEventHandler;
import ru.yandex.practicum.handlers.sensor.GrpcSensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
@Slf4j
@SuppressWarnings("unused")
public class CollectorController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<SensorEventProto.PayloadCase, GrpcSensorEventHandler> sensorHandlers;
    private final Map<HubEventProto.PayloadCase, GrpcHubEventHandler> hubHandlers;

    public CollectorController(Set<GrpcSensorEventHandler> sensorHandlers,
                               Set<GrpcHubEventHandler> hubHandlers) {
        this.sensorHandlers = sensorHandlers.stream()
                .collect(Collectors.toMap(GrpcSensorEventHandler::getMessageType, Function.identity()));
        this.hubHandlers = hubHandlers.stream()
                .collect(Collectors.toMap(GrpcHubEventHandler::getMessageType, Function.identity()));
    }

    @PostConstruct
    public void init() {
        log.info("CollectorController gRPC service is initialized");
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        if (request == null) {
            handleError("Пустой запрос сенсора", null, responseObserver);
            return;
        }
        try {
            GrpcSensorEventHandler handler = sensorHandlers.get(request.getPayloadCase());
            if (handler == null) {
                throw new IllegalArgumentException("Неизвестный тип сенсора: " + request.getPayloadCase());
            }
            handler.handle(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            handleError("Ошибка обработки события сенсора", e, responseObserver);
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        if (request == null) {
            handleError("Пустой запрос хаба", null, responseObserver);
            return;
        }
        try {
            GrpcHubEventHandler handler = hubHandlers.get(request.getPayloadCase());
            if (handler == null) {
                throw new IllegalArgumentException("Неизвестный тип хаба: " + request.getPayloadCase());
            }
            handler.handle(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            handleError("Ошибка обработки события хаба", e, responseObserver);
        }
    }

    private void handleError(String message, Exception e, StreamObserver<?> responseObserver) {
        log.error(message, e);
        Status status;
        status = Status.INTERNAL.withDescription(message);
        if (e != null) {
            status = status.withCause(e);
        }
        responseObserver.onError(status.asRuntimeException());
    }
}