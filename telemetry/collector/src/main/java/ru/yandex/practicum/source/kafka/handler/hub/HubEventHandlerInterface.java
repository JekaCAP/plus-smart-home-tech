package ru.yandex.practicum.source.kafka.handler.hub;

import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;

public interface HubEventHandlerInterface {
    HubEventType getMessageType();

    void handle(HubEvent event);
}