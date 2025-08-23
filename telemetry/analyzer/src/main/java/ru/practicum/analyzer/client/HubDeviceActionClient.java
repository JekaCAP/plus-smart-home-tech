package ru.practicum.analyzer.client;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.model.ScenarioAction;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequestProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

import java.time.Instant;

/**
 * Клиент для отправки команд на выполнение действий устройств через gRPC HubRouter.
 * <p>
 * Предназначен для формирования и отправки {@link DeviceActionRequestProto} с информацией
 * о конкретном действии {@link ScenarioAction} для заданного хаба и сценария.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HubDeviceActionClient {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub stub;

    public void sendDeviceAction(String hubId, String scenarioName, ScenarioAction action) {
        try {
            DeviceActionProto actionProto = buildDeviceAction(action);
            DeviceActionRequestProto request = buildRequest(hubId, scenarioName, actionProto);

            stub.handleDeviceAction(request);
            log.info("gRPC hub-router: hubId='{}', scenario='{}', action={}",
                    hubId, scenarioName, actionProto);

        } catch (Exception ex) {
            log.error("Ошибка отправки в hub-router: {}", ex.getMessage(), ex);
        }
    }

    private DeviceActionProto buildDeviceAction(ScenarioAction action) {
        DeviceActionProto.Builder builder = DeviceActionProto.newBuilder()
                .setSensorId(action.getId().getSensorId())
                .setType(ActionTypeProto.valueOf(action.getAction().getType().name()));

        Integer val = action.getAction().getValue();
        if (val != null) builder.setValue(val);

        return builder.build();
    }

    private DeviceActionRequestProto buildRequest(String hubId, String scenarioName, DeviceActionProto actionProto) {
        return DeviceActionRequestProto.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(actionProto)
                .setTimestamp(currentTimestamp())
                .build();
    }

    private Timestamp currentTimestamp() {
        Instant now = Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();
    }
}