package ru.yandex.practicum.handlers.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.config.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mapper.ProtobufToAvroMapper;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractHubEventHandler implements GrpcHubEventHandler {

    protected final ProtobufToAvroMapper mapper;
    protected final KafkaEventProducer producer;

    @Override
    public void handle(HubEventProto event) {
        if (event == null) {
            log.warn("Получен null hub event, обработка пропущена");
            return;
        }

        log.info("Обработка хаб-события: type={}, hubId={}", getMessageType(), event.getHubId());

        try {
            HubEventAvro avro = mapper.toAvro(event);
            if (avro == null) {
                log.warn("Mapper вернул null для события hubId={}, пропуск отправки", event.getHubId());
                return;
            }
            producer.sendHubEvent(avro);
            log.info("Событие хаба успешно отправлено в Kafka: hubId={}", event.getHubId());
        } catch (Exception e) {
            log.error("Ошибка при обработке хаб-события: hubId={}", event.getHubId(), e);
        }
    }
}