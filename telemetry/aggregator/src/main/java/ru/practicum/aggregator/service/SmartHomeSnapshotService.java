package ru.practicum.aggregator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Сервис для управления снапшотами состояния датчиков умного дома.
 * <p>
 * Формирует текущие состояния всех датчиков конкретного хаба на основе поступающих событий.
 * При изменении состояния датчика обновляется соответствующий снапшот.
 * </p>
 *
 * <ul>
 *   <li>Агрегация событий датчиков в снапшоты</li>
 *   <li>Обновление состояния датчиков в существующих снапшотах</li>
 *   <li>Возврат Optional с обновлённым снапшотом только при изменении данных</li>
 * </ul>
 */
@RequiredArgsConstructor
@Service
public class SmartHomeSnapshotService {

    private final Map<String, SensorsSnapshotAvro> snapshotsByHubId = new ConcurrentHashMap<>();

    /**
     * Обновляет снапшот состояния датчика по событию.
     *
     * @param sensorEvent событие датчика
     * @return {@link Optional} с обновлённым снапшотом, если данные изменились, иначе пустой
     */
    public Optional<SensorsSnapshotAvro> updateSnapshot(SensorEventAvro sensorEvent) {
        SensorsSnapshotAvro currentSnapshot = snapshotsByHubId.computeIfAbsent(
                sensorEvent.getHubId(),
                hubId -> SensorsSnapshotAvro.newBuilder()
                        .setHubId(hubId)
                        .setSensorsState(new HashMap<>())
                        .setTimestamp(sensorEvent.getTimestamp())
                        .build()
        );

        SensorStateAvro previousState = currentSnapshot.getSensorsState().get(sensorEvent.getId());

        if (previousState != null &&
            (previousState.getTimestamp().isAfter(sensorEvent.getTimestamp()) ||
             previousState.getData().equals(sensorEvent.getPayload()))) {
            return Optional.empty();
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(sensorEvent.getTimestamp())
                .setData(sensorEvent.getPayload())
                .build();

        currentSnapshot.getSensorsState().put(sensorEvent.getId(), newState);
        currentSnapshot.setTimestamp(sensorEvent.getTimestamp());

        return Optional.of(currentSnapshot);
    }
}