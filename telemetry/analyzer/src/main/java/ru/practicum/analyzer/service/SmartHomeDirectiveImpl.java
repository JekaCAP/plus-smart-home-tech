package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.client.HubDeviceActionClient;
import ru.practicum.analyzer.model.Condition;
import ru.practicum.analyzer.model.Scenario;
import ru.practicum.analyzer.model.enums.ConditionOperation;
import ru.practicum.analyzer.model.enums.ConditionType;
import ru.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.util.List;
import java.util.Map;

/**
 * Сервис для обработки снимков состояния сенсоров (SensorsSnapshotAvro)
 * и выполнения сценариев умного дома, если условия сценариев выполняются.
 * <p>
 * Проверяет условия всех сценариев для конкретного хаба, используя актуальные значения сенсоров,
 * и при успешном выполнении условий вызывает HubDeviceActionClient для отправки действий на устройства.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmartHomeDirectiveImpl implements SmartHomeDirective {

    private final ScenarioRepository scenarioRepository;
    private final HubDeviceActionClient hubRouterClient;

    /**
     * Обновляет состояния всех сценариев на основании полученного снимка сенсоров.
     * <p>
     * Для каждого сценария проверяются все условия. Если все условия выполнены,
     * выполняются все действия сценария через HubDeviceActionClient.
     *
     * @param snapshot Снимок состояния всех сенсоров хаба
     */
    @Override
    @Transactional(readOnly = true)
    public void update(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        Map<String, SensorStateAvro> stateMap = snapshot.getSensorsState();

        log.info("Получен снимок состояния от hubId: {}", hubId);

        List<Scenario> scenarios = scenarioRepository.findAllByHubId(hubId);
        log.info("Найдено сценариев для hubId {}: {}", hubId, scenarios.size());

        scenarios.forEach(scenario -> processScenario(scenario, hubId, stateMap));
    }

    /**
     * Проверяет условия конкретного сценария и выполняет действия, если условия выполнены.
     *
     * @param scenario Сценарий для проверки
     * @param hubId    Идентификатор хаба
     * @param stateMap Карта состояний сенсоров
     */
    private void processScenario(Scenario scenario, String hubId, Map<String, SensorStateAvro> stateMap) {
        log.debug("Проверяем сценарий: {}", scenario.getName());

        boolean allConditionsOk = scenario.getConditions().stream()
                .allMatch(sc -> checkCondition(sc.getCondition(), sc.getSensor().getId(), stateMap));

        if (allConditionsOk) {
            executeActions(scenario, hubId);
        } else {
            log.info("Условия не выполнены, действий по сценарию '{}' не будет", scenario.getName());
        }
    }

    /**
     * Проверяет одно условие конкретного сенсора.
     *
     * @param condition Условие сценария
     * @param sensorId  Идентификатор сенсора
     * @param stateMap  Карта состояний сенсоров
     * @return true, если условие выполнено, иначе false
     */
    private boolean checkCondition(Condition condition, String sensorId, Map<String, SensorStateAvro> stateMap) {
        SensorStateAvro sensorState = stateMap.get(sensorId);
        if (sensorState == null) {
            log.warn("Нет состояния для датчика {}, пропускаем условие", sensorId);
            return false;
        }

        int actual = extractValue(sensorState, condition);
        boolean ok = evaluate(condition.getOperation(), actual, condition.getValue());

        log.debug("датчик={} тип={} операция={} порог={} текущее={} --- {}",
                sensorId,
                condition.getType(),
                condition.getOperation(),
                condition.getValue(),
                actual,
                ok ? "OK" : "FAIL");

        return ok;
    }

    /**
     * Выполняет все действия сценария через HubDeviceActionClient.
     *
     * @param scenario Сценарий, действия которого нужно выполнить
     * @param hubId    Идентификатор хаба
     */
    private void executeActions(Scenario scenario, String hubId) {
        scenario.getActions().forEach(action ->
                hubRouterClient.sendDeviceAction(hubId, scenario.getName(), action));

        log.debug("Выполнено {} действий для сценария '{}'", scenario.getActions().size(), scenario.getName());
    }

    /**
     * Извлекает актуальное числовое значение сенсора в зависимости от типа сенсора и типа условия.
     *
     * @param state     Состояние сенсора
     * @param condition Условие сценария
     * @return Значение сенсора для проверки условия
     * @throws IllegalArgumentException если тип сенсора неизвестен
     */
    private int extractValue(SensorStateAvro state, Condition condition) {
        Object data = state.getData();
        ConditionType type = condition.getType();

        if (data instanceof ClimateSensorAvro c) return switch (type) {
            case TEMPERATURE -> c.getTemperatureC();
            case HUMIDITY -> c.getHumidity();
            case CO2LEVEL -> c.getCo2Level();
            default -> logWarning(type, "ClimateSensorAvro");
        };
        if (data instanceof TemperatureSensorAvro t) return switch (type) {
            case TEMPERATURE -> t.getTemperatureC();
            default -> logWarning(type, "TemperatureSensorAvro");
        };
        if (data instanceof LightSensorAvro l) return switch (type) {
            case LUMINOSITY -> l.getLuminosity();
            default -> logWarning(type, "LightSensorAvro");
        };
        if (data instanceof MotionSensorAvro m) return switch (type) {
            case MOTION -> m.getMotion() ? 1 : 0;
            default -> logWarning(type, "MotionSensorAvro");
        };
        if (data instanceof SwitchSensorAvro s) return switch (type) {
            case SWITCH -> s.getState() ? 1 : 0;
            default -> logWarning(type, "SwitchSensorAvro");
        };

        throw new IllegalArgumentException("Неизвестный тип данных сенсора: " + data.getClass().getSimpleName());
    }

    /**
     * Логирует предупреждение о неподдерживаемом сочетании типа сенсора и условия.
     *
     * @param type       Тип условия
     * @param sensorType Тип сенсора
     * @return 0 как значение по умолчанию
     */
    private int logWarning(ConditionType type, String sensorType) {
        log.warn("Тип условия {} неприменим к {}", type, sensorType);
        return 0;
    }

    /**
     * Оценивает условие: сравнивает фактическое значение с целевым.
     *
     * @param op     Операция условия
     * @param actual Фактическое значение сенсора
     * @param target Целевое значение условия
     * @return true, если условие выполнено
     */
    private boolean evaluate(ConditionOperation op, int actual, int target) {
        return switch (op) {
            case EQUALS -> actual == target;
            case GREATER_THAN -> actual > target;
            case LOWER_THAN -> actual < target;
        };
    }
}
