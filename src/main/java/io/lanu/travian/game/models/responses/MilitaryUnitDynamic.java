package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.EMilitaryUnitState;
import io.lanu.travian.enums.ENation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MilitaryUnitDynamic extends MilitaryUnit{

    private String mission;
    private VillageBrief targetVillage;
    private LocalDateTime executionTime;
    private int duration;

    public MilitaryUnitDynamic(String id, ENation nation, boolean move, EMilitaryUnitState state, VillageBrief homeVillage,
                               int[] units, String mission, VillageBrief targetVillage, LocalDateTime executionTime, int duration) {
        super(id, nation, move, state, homeVillage, units);
        this.mission = mission;
        this.targetVillage = targetVillage;
        this.executionTime = executionTime;
        this.duration = duration;
    }
}
