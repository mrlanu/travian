package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.ENation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MilitaryUnitEntityDynamic extends MilitaryUnitEntity {
    private String targetVillageId;
    private String targetVillageName;
    private String targetPlayerName;
    private int[] targetVillageCoordinates;
    private int duration;

    public MilitaryUnitEntityDynamic(ENation nation, boolean move, String mission,
                                     String originVillageId, String originVillageName, String originPlayerName, int[] originVillageCoordinates,
                                     int[] units, LocalDateTime executionTime, String targetVillageId,
                                     String targetVillageName, String targetPlayerName, int[] targetVillageCoordinates, int duration) {
        super(nation, move, mission, originVillageId, originVillageName, originPlayerName, originVillageCoordinates, units, executionTime);
        this.targetVillageId = targetVillageId;
        this.targetVillageName = targetVillageName;
        this.targetPlayerName = targetPlayerName;
        this.targetVillageCoordinates = targetVillageCoordinates;
        this.duration = duration;
    }
}