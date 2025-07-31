package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.hub.DeviceAddedEvent;
import ru.yandex.practicum.model.hub.DeviceRemovedEvent;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.model.sensor.LightSensorEvent;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface EventMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "payload", source = "event", qualifiedBy = ToPayload.class)
    SensorEventAvro toAvro(ClimateSensorEvent event);

    @ToPayload
    @Mapping(target = "temperatureC", source = "temperatureC")
    @Mapping(target = "humidity", source = "humidity")
    @Mapping(target = "co2Level", source = "co2Level")
    ClimateSensorAvro toPayload(ClimateSensorEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "payload", source = "event", qualifiedBy = ToPayload.class)
    SensorEventAvro toAvro(LightSensorEvent event);

    @ToPayload
    @Mapping(target = "linkQuality", source = "linkQuality")
    @Mapping(target = "luminosity", source = "luminosity")
    LightSensorAvro toPayload(LightSensorEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "payload", source = "event", qualifiedBy = ToPayload.class)
    SensorEventAvro toAvro(MotionSensorEvent event);

    @ToPayload
    @Mapping(target = "linkQuality", source = "linkQuality")
    @Mapping(target = "motion", source = "motion")
    @Mapping(target = "voltage", source = "voltage")
    MotionSensorAvro toPayload(MotionSensorEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "payload", source = "event", qualifiedBy = ToPayload.class)
    SensorEventAvro toAvro(SwitchSensorEvent event);

    @ToPayload
    @Mapping(target = "state", source = "state")
    SwitchSensorAvro toPayload(SwitchSensorEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "payload", source = "event", qualifiedBy = ToPayload.class)
    SensorEventAvro toAvro(TemperatureSensorEvent event);

    @ToPayload
    @Mapping(target = "temperatureC", source = "temperatureC")
    @Mapping(target = "temperatureF", source = "temperatureF")
    TemperatureSensorAvro toPayload(TemperatureSensorEvent event);

    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "payload", source = "event", qualifiedBy = ToPayload.class)
    HubEventAvro toAvro(DeviceAddedEvent event);

    @ToPayload
    @Mapping(target = "id", source = "id")
    @Mapping(target = "type", source = "deviceType")
    DeviceAddedEventAvro toPayload(DeviceAddedEvent event);

    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "payload", source = "event", qualifiedBy = ToPayload.class)
    HubEventAvro toAvro(DeviceRemovedEvent event);

    @ToPayload
    @Mapping(target = "id", source = "id")
    DeviceRemovedEventAvro toPayload(DeviceRemovedEvent event);

    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "payload", source = "event", qualifiedBy = ToPayload.class)
    HubEventAvro toAvro(ScenarioAddedEvent event);

    @ToPayload
    @Mapping(target = "name", source = "name")
    @Mapping(target = "conditions", source = "conditions")
    @Mapping(target = "actions", source = "actions")
    ScenarioAddedEventAvro toPayload(ScenarioAddedEvent event);

    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "payload", source = "event", qualifiedBy = ToPayload.class)
    HubEventAvro toAvro(ScenarioRemovedEvent event);

    @ToPayload
    @Mapping(target = "name", source = "name")
    ScenarioRemovedEventAvro toPayload(ScenarioRemovedEvent event);
}