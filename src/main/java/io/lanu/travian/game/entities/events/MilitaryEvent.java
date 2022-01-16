package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EMilitaryUnitState;
import io.lanu.travian.enums.ENation;
import io.lanu.travian.game.entities.VillageEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("military")
public class MilitaryEvent implements IEvent{
    @Id
    private String id;
    private ENation nation;
    private boolean move;
    private EMilitaryUnitState state;
    private String mission;
    private String originVillageId;
    private String originVillageName;
    private String originPlayerName;
    private int[] originVillageCoordinates;
    private String targetVillageId;
    private String targetVillageName;
    private String targetPlayerName;
    private int[] targetVillageCoordinates;
    private String currentLocationVillageId;
    private LocalDateTime executionTime;
    private int duration;
    private int eatExpenses;
    private int[] units;

    public MilitaryEvent(ENation nation, boolean move, EMilitaryUnitState state, String mission, String originVillageId,
                         String originVillageName, String originPlayerName, int[] originVillageCoordinates, String targetVillageId,
                         String targetVillageName, String targetPlayerName, int[] targetVillageCoordinates,
                         String currentLocationVillageId, LocalDateTime executionTime, int duration, int eatExpenses, int[] units) {
        this.nation = nation;
        this.move = move;
        this.state = state;
        this.mission = mission;
        this.originVillageId = originVillageId;
        this.originVillageName = originVillageName;
        this.originPlayerName = originPlayerName;
        this.originVillageCoordinates = originVillageCoordinates;
        this.targetVillageId = targetVillageId;
        this.targetVillageName = targetVillageName;
        this.targetPlayerName = targetPlayerName;
        this.targetVillageCoordinates = targetVillageCoordinates;
        this.currentLocationVillageId = currentLocationVillageId;
        this.executionTime = executionTime;
        this.duration = duration;
        this.eatExpenses = eatExpenses;
        this.units = units;
    }

    @Override
    public void execute(VillageEntity villageEntity) {

    }

    @Override
    public LocalDateTime getExecutionTime() {
        return executionTime;
    }
}
