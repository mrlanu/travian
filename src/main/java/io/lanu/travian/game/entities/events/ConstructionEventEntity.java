package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EBuilding;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ConstructionEventEntity {

    @Id
    private String eventId = UUID.randomUUID().toString();
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
