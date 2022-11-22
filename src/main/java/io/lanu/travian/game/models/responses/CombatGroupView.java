package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.ECombatGroupLocation;
import io.lanu.travian.enums.ECombatGroupMission;
import io.lanu.travian.enums.ENation;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CombatGroupView {

    protected String id;
    protected ENation nation;
    private ECombatGroupMission mission;
    protected boolean move;
    protected ECombatGroupLocation state;
    protected VillageBrief origin;
    protected VillageBrief target;
    protected int[] units;

    public CombatGroupView(String id, ENation nation, ECombatGroupMission mission, boolean move, ECombatGroupLocation state,
                           VillageBrief origin, VillageBrief target, int[] units) {
        this.id = id;
        this.nation = nation;
        this.mission = mission;
        this.move = move;
        this.state = state;
        this.origin = origin;
        this.target = target;
        this.units = units;
    }
}
