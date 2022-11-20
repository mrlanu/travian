package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.EMilitaryUnitLocation;
import io.lanu.travian.enums.ECombatUnitMission;
import io.lanu.travian.enums.ENation;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MilitaryUnitView {

    protected String id;
    protected ENation nation;
    private ECombatUnitMission mission;
    protected boolean move;
    protected EMilitaryUnitLocation state;
    protected VillageBrief origin;
    protected VillageBrief target;
    protected int[] units;

    public MilitaryUnitView(String id, ENation nation, ECombatUnitMission mission, boolean move, EMilitaryUnitLocation state,
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
