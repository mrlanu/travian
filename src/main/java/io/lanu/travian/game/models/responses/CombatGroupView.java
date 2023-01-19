package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.ECombatGroupLocation;
import io.lanu.travian.enums.ECombatGroupMission;
import io.lanu.travian.enums.ENation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CombatGroupView {

    protected String id;
    protected ENation nation;
    private ECombatGroupMission mission;
    protected boolean move;
    protected ECombatGroupLocation state;
    protected VillageBrief from;
    protected VillageBrief to;
    protected List<Integer> units;

    public CombatGroupView(String id, ENation nation, ECombatGroupMission mission, boolean move, ECombatGroupLocation state,
                           VillageBrief origin, VillageBrief target, List<Integer> units) {
        this.id = id;
        this.nation = nation;
        this.mission = mission;
        this.move = move;
        this.state = state;
        this.from = origin;
        this.to = target;
        this.units = units;
    }
}
