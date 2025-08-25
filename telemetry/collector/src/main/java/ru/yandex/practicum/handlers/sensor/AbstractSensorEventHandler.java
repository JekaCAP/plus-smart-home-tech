package ru.yandex.practicum.handlers.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.config.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mapper.ProtobufToAvroMapper;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSensorEventHandler implements GrpcSensorEventHandler {

    protected final ProtobufToAvroMapper mapper;
    protected final KafkaEventProducer producer;

    @Override
    public void handle(SensorEventProto event) {
        if (event == null) {
            log.warn("Получен null event, обработка пропущена");
            return;
        }

        if (event.getPayloadCase() != getMessageType()) {
            log.warn("Неподходящий тип события: ожидается={}, получен={}",
                    getMessageType(), event.getPayloadCase());
            return;
        }

        log.info("Обработка сенсорного события: type={}, id={}", getMessageType(), event.getId());

        try {
            SensorEventAvro avro = mapper.toAvro(event);
            if (avro == null) {
                log.warn("Mapper вернул null для события id={}, пропуск отправки", event.getId());
                return;
            }
            producer.sendSensorEvent(avro);
            log.info("Событие сенсора успешно отправлено в Kafka: id={}", event.getId());
        } catch (Exception e) {
            log.error("Ошибка при обработке сенсорного события: id={}", event.getId(), e);
        }
    }
}