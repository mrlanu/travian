package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EBuilding;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
/*@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FieldUpgradeBuildEvent.class, name = "fieldUpgradeEvent"),
        @JsonSubTypes.Type(value = TroopBuildEvent.class, name = "troopEvent")
})*/
@Document("construction-events")
public class ConstructionEventEntity {

    @Id
    private String eventId;
    private int buildingPosition;
    private EBuilding buildingName;
    private int toLevel;
    private String villageId;
    private LocalDateTime executionTime;

    public ConstructionEventEntity(int buildingPosition, EBuilding buildingName, int toLevel, String villageId, LocalDateTime executionTime) {
        this.buildingPosition = buildingPosition;
        this.buildingName = buildingName;
        this.toLevel = toLevel;
        this.villageId = villageId;
        this.executionTime = executionTime;
    }
}
