package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.ECombatGroupLocation;
import io.lanu.travian.enums.ECombatGroupMission;
import io.lanu.travian.enums.ENation;
import io.lanu.travian.enums.EResource;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class CombatGroupMovedView extends CombatGroupView {

    private Map<EResource, BigDecimal> plunder;
    private LocalDateTime executionTime;
    private int duration;

    public CombatGroupMovedView(String id, ENation nation, ECombatGroupMission mission, boolean move, ECombatGroupLocation state,
                                VillageBrief from, VillageBrief to,
                                int[] units, Map<EResource, BigDecimal> plunder, LocalDateTime executionTime, int duration) {
        super(id, nation, mission, move, state, from, to, units);
        this.to = to;
        this.plunder = plunder;
        this.executionTime = executionTime;
        this.duration = duration;
    }
}
