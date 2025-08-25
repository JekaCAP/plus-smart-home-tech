package ru.practicum.analyzer.service;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

/**
 * Интерфейс для обработки снимков состояния сенсоров умного дома
 * и выполнения соответствующих сценариев.
 * <p>
 * Реализация интерфейса должна проверять условия всех сценариев
 * и инициировать действия на устройствах, если условия выполнены.
 */
public interface SmartHomeDirective {

    /**
     * Обновляет состояния сценариев на основании полученного снимка сенсоров.
     * <p>
     * Метод вызывается при получении нового {@link SensorsSnapshotAvro} от хаба.
     * Реализация должна проверить условия сценариев и при необходимости
     * выполнить действия на устройствах.
     *
     * @param snapshotAvro Снимок состояния всех сенсоров хаба
     */
    void update(SensorsSnapshotAvro snapshotAvro);
}
