package ru.yandex.practicum.handlers.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface GrpcHubEventHandler {
    HubEventProto.PayloadCase getMessageType();
    void handle(HubEventProto event);
}