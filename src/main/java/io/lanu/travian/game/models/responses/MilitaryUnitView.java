package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.EMilitaryUnitState;
import io.lanu.travian.enums.ENation;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MilitaryUnitView {

    protected String id;
    protected ENation nation;
    protected boolean move;
    protected EMilitaryUnitState state;
    protected VillageBrief originVillage;
    protected int[] units;

    public MilitaryUnitView(String id, ENation nation, boolean move, EMilitaryUnitState state, VillageBrief originVillage, int[] units) {
        this.id = id;
        this.nation = nation;
        this.move = move;
        this.state = state;
        this.originVillage = originVillage;
        this.units = units;
    }
}
