package ru.yandex.practicum.source.kafka.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.source.kafka.handler.Config;

@Slf4j
@RequiredArgsConstructor
public abstract class SensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandlerInterface {
    protected final Config.KafkaEventProducer producer;
    protected final Config topics;

    protected abstract T mapToAvro(SensorEvent event);

    @Override
    public void handle(SensorEvent event) {
        T avroEvent = mapToAvro(event);
        String topic = topics.getProducer().getTopics().get(Config.TopicType.SENSORS_EVENTS);
        log.info("Handling event of type {}. Will send to topic {}", getMessageType(), topic);
        producer.send(topic, event.getId(), avroEvent);
    }
}