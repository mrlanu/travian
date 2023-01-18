package io.lanu.travian.game.entities.events;

import io.lanu.travian.game.models.buildings.BuildingsID;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ConstructionEventEntity {

    @Id
    private String eventId = UUID.randomUUID().toString();
    private int buildingPosition;
    private BuildingsID buildingID;
    private int toLevel;
    private String villageId;
    private LocalDateTime executionTime;

    public ConstructionEventEntity(int buildingPosition, BuildingsID buildingID, int toLevel, String villageId, LocalDateTime executionTime) {
        this.buildingPosition = buildingPosition;
        this.buildingID = buildingID;
        this.toLevel = toLevel;
        this.villageId = villageId;
        this.executionTime = executionTime;
    }
}
