package ru.yandex.practicum.source.kafka.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.source.kafka.handler.Config;

@Slf4j
@RequiredArgsConstructor
public abstract class HubEventHandler<T extends SpecificRecordBase> implements HubEventHandlerInterface {
    protected final Config.KafkaEventProducer producer;
    protected final Config topics;

    protected abstract T mapToAvro(HubEvent event);

    @Override
    public void handle(HubEvent event) {
        T avroEvent = mapToAvro(event);
        String topic = topics.getProducer().getTopics().get(Config.TopicType.HUBS_EVENTS);
        log.info("Hub event {}. Topic {}", getMessageType(), topic);
        producer.send(topic, event.getHubId(), avroEvent);
    }
}