package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.EMilitaryUnitLocation;
import io.lanu.travian.enums.ENation;
import io.lanu.travian.enums.EResource;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class MovedMilitaryUnitView extends MilitaryUnitView {

    private Map<EResource, BigDecimal> plunder;
    private LocalDateTime executionTime;
    private int duration;

    public MovedMilitaryUnitView(String id, ENation nation, String mission, boolean move, EMilitaryUnitLocation state,
                                 VillageBrief origin, VillageBrief target,
                                 int[] units, Map<EResource, BigDecimal> plunder, LocalDateTime executionTime, int duration) {
        super(id, nation, mission, move, state, origin, target, units);
        this.target = target;
        this.plunder = plunder;
        this.executionTime = executionTime;
        this.duration = duration;
    }
}
