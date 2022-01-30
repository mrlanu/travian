package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EMilitaryUnitState;
import io.lanu.travian.enums.ENation;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.responses.VillageBrief;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("military")
@NoArgsConstructor
public class MilitaryUnitEntity {
    @Id
    private String id;
    private ENation nation;
    private boolean move;
    private String mission;
    private int[] units;

    private String originVillageId;
    private VillageBrief origin;

    private String targetVillageId;
    private VillageBrief target;

    private LocalDateTime executionTime;
    private int duration;
    private int eatExpenses;

    public MilitaryUnitEntity(ENation nation, boolean move, String mission, int[] units, String originVillageId,
                              VillageBrief origin, String targetVillageId, VillageBrief target, LocalDateTime executionTime,
                              int duration, int eatExpenses) {
        this.nation = nation;
        this.move = move;
        this.mission = mission;
        this.units = units;
        this.originVillageId = originVillageId;
        this.origin = origin;
        this.targetVillageId = targetVillageId;
        this.target = target;
        this.executionTime = executionTime;
        this.duration = duration;
        this.eatExpenses = eatExpenses;
    }
}
