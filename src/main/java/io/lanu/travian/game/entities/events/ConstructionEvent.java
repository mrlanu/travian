package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EBuildings;
import io.lanu.travian.game.entities.VillageEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
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
public class ConstructionEvent implements IEvent {

    @Id
    private String eventId;
    private int buildingPosition;
    private EBuildings buildingName;
    private int toLevel;
    private String villageId;
    private LocalDateTime executionTime;

    public ConstructionEvent(int buildingPosition, EBuildings buildingName, int toLevel, String villageId, LocalDateTime executionTime) {
        this.buildingPosition = buildingPosition;
        this.buildingName = buildingName;
        this.toLevel = toLevel;
        this.villageId = villageId;
        this.executionTime = executionTime;
    }

    @Override
    public void execute(VillageEntity villageEntity) {
        var build = villageEntity.getBuildings().get(buildingPosition);
        build.setLevel(build.getLevel() + 1);
    }
}
