package io.lanu.travian.game.entities;

import io.lanu.travian.enums.EMilitaryUnitState;
import io.lanu.travian.enums.ENation;
import io.lanu.travian.game.models.responses.MilitaryUnitContract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document("military")
public class MilitaryUnitEntity {
    @Id
    private String id;
    private ENation nation;
    private boolean move;
    private EMilitaryUnitState state;
    private String mission;
    private String originVillageId;
    private String originVillageName;
    private int[] originVillageCoordinates;
    private String targetVillageId;
    private String targetVillageName;
    private String currentLocationVillageId;
    private LocalDateTime arrivalTime;
    private int duration;
    private int eatExpenses;
    private int[] units;

    public MilitaryUnitEntity(ENation nation, boolean move, EMilitaryUnitState state, String mission, String originVillageId, String originVillageName,
                              int[] originVillageCoordinates, String targetVillageId, String targetVillageName,
                              String currentLocationVillageId, LocalDateTime arrivalTime, int duration, int eatExpenses, int[] units) {
        this.nation = nation;
        this.move = move;
        this.state = state;
        this.mission = mission;
        this.originVillageId = originVillageId;
        this.originVillageName = originVillageName;
        this.originVillageCoordinates = originVillageCoordinates;
        this.targetVillageId = targetVillageId;
        this.targetVillageName = targetVillageName;
        this.currentLocationVillageId = currentLocationVillageId;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.eatExpenses = eatExpenses;
        this.units = units;
    }
}
