package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.hub.enums.DeviceActionType;

@Getter
@Setter
@ToString
public class DeviceAction {
    @NotBlank
    private String sensorId;

    @NotBlank
    private DeviceActionType type;

    @NotBlank
    private int value;
}