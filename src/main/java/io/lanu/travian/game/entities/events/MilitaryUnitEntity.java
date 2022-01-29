package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EMilitaryUnitState;
import io.lanu.travian.enums.ENation;
import io.lanu.travian.game.entities.VillageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("military")
@NoArgsConstructor
public abstract class MilitaryUnitEntity {
    @Id
    protected String id;
    protected ENation nation;
    protected boolean move;
    protected String mission;
    protected String originVillageId;
    protected String originVillageName;
    protected String originPlayerName;
    protected int[] originVillageCoordinates;
    protected int[] units;
    protected LocalDateTime executionTime;

    public MilitaryUnitEntity(ENation nation, boolean move, String mission, String originVillageId,
                              String originVillageName, String originPlayerName, int[] originVillageCoordinates,
                              int[] units, LocalDateTime executionTime) {
        this.nation = nation;
        this.move = move;
        this.mission = mission;
        this.originVillageId = originVillageId;
        this.originVillageName = originVillageName;
        this.originPlayerName = originPlayerName;
        this.originVillageCoordinates = originVillageCoordinates;
        this.units = units;
        this.executionTime = executionTime;
    }
}
