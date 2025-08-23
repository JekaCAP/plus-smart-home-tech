package ru.practicum.analyzer.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@Embeddable
public class ScenarioActionId {

    @NotNull
    private Long scenarioId;

    @NotNull
    private String sensorId;

    @NotNull
    private Long actionId;
}
