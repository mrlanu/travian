package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.EMilitaryUnitState;
import io.lanu.travian.enums.ENation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MovedMilitaryUnitView extends MilitaryUnitView {

    private LocalDateTime executionTime;
    private int duration;

    public MovedMilitaryUnitView(String id, ENation nation, String mission, boolean move, EMilitaryUnitState state,
                                 VillageBrief origin, VillageBrief target,
                                 int[] units, LocalDateTime executionTime, int duration) {
        super(id, nation, mission, move, state, origin, target, units);
        this.target = target;
        this.executionTime = executionTime;
        this.duration = duration;
    }
}
