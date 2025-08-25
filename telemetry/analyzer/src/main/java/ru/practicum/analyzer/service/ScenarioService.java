package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.mapper.AvroToEntityMapper;
import ru.practicum.analyzer.model.Action;
import ru.practicum.analyzer.model.Condition;
import ru.practicum.analyzer.model.Scenario;
import ru.practicum.analyzer.model.ScenarioAction;
import ru.practicum.analyzer.model.ScenarioActionId;
import ru.practicum.analyzer.model.ScenarioCondition;
import ru.practicum.analyzer.model.ScenarioConditionId;
import ru.practicum.analyzer.model.Sensor;
import ru.practicum.analyzer.repository.ActionRepository;
import ru.practicum.analyzer.repository.ConditionRepository;
import ru.practicum.analyzer.repository.ScenarioActionRepository;
import ru.practicum.analyzer.repository.ScenarioConditionRepository;
import ru.practicum.analyzer.repository.ScenarioRepository;
import ru.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

/**
 * Сервис для управления сценариями умного дома.
 * <p>
 * Отвечает за создание, обновление и удаление сценариев, их условий и действий.
 * Получает данные из событий HubEventAvro и сохраняет их в соответствующие
 * сущности базы данных.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioConditionRepository scenarioConditionRepo;
    private final ScenarioActionRepository scenarioActionRepo;
    private final AvroToEntityMapper mapper;

    @Transactional
    public void saveOrUpdateScenario(HubEventAvro hubEvent) {
        if (hubEvent == null || hubEvent.getPayload() == null) {
            log.warn("Неверное событие: hubEvent или payload == null");
            return;
        }

        String hubId = hubEvent.getHubId();
        ScenarioAddedEventAvro evt = (ScenarioAddedEventAvro) hubEvent.getPayload();
        log.info("ScenarioAddedEventAvro: {}", evt);

        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, evt.getName())
                .orElseGet(() -> mapper.toScenario(hubId, evt));
        scenario.setHubId(hubId);
        scenario.setName(evt.getName());
        scenario = scenarioRepository.save(scenario);

        log.info("Сохранён сценарий: {}", scenario);

        clearExistingScenarioData(scenario.getId());

        saveConditions(evt, scenario, hubId);
        saveActions(evt, scenario, hubId);

        log.info("Сценарий: {} для хаба: {} сохранён/обновлён: условия = {}, действия = {}",
                scenario.getName(), hubId,
                evt.getConditions() != null ? evt.getConditions().size() : 0,
                evt.getActions() != null ? evt.getActions().size() : 0);
    }

    @Transactional
    public void removeScenario(String hubId, String name) {
        scenarioRepository.findByHubIdAndName(hubId, name).ifPresent(s -> {
            clearExistingScenarioData(s.getId());
            scenarioRepository.delete(s);
            log.info("Сценарий: {} для хаба: {} удалён.", name, hubId);
        });
    }

    private void clearExistingScenarioData(Long scenarioId) {
        scenarioConditionRepo.deleteByIdScenarioId(scenarioId);
        scenarioActionRepo.deleteByIdScenarioId(scenarioId);
    }

    private void saveConditions(ScenarioAddedEventAvro evt, Scenario scenario, String hubId) {
        if (evt.getConditions() == null) return;

        evt.getConditions().forEach(avro -> {
            Sensor sensor = findOrCreateSensor(avro.getSensorId(), hubId);

            Condition condition = mapper.toCondition(avro);
            condition = conditionRepository.save(condition);

            ScenarioCondition sc = ScenarioCondition.builder()
                    .id(new ScenarioConditionId(scenario.getId(), sensor.getId(), condition.getId()))
                    .scenario(scenario)
                    .sensor(sensor)
                    .condition(condition)
                    .build();
            scenarioConditionRepo.save(sc);
        });
    }

    private void saveActions(ScenarioAddedEventAvro evt, Scenario scenario, String hubId) {
        if (evt.getActions() == null) return;

        evt.getActions().forEach(avro -> {
            Sensor sensor = findOrCreateSensor(avro.getSensorId(), hubId);

            Action action = mapper.toAction(avro);
            action = actionRepository.save(action);

            ScenarioAction sa = ScenarioAction.builder()
                    .id(new ScenarioActionId(scenario.getId(), sensor.getId(), action.getId()))
                    .scenario(scenario)
                    .sensor(sensor)
                    .action(action)
                    .build();
            scenarioActionRepo.save(sa);
        });
    }

    private Sensor findOrCreateSensor(String sensorId, String hubId) {
        return sensorRepository.findById(sensorId)
                .orElseGet(() -> {
                    Sensor s = Sensor.builder().id(sensorId).hubId(hubId).build();
                    log.info("Автосоздаём сенсор: {} для хаба: {}", sensorId, hubId);
                    return sensorRepository.save(s);
                });
    }
}