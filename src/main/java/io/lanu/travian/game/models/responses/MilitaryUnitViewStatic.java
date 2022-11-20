package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.EMilitaryUnitLocation;
import io.lanu.travian.enums.ECombatUnitMission;
import io.lanu.travian.enums.ENation;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MilitaryUnitViewStatic extends MilitaryUnitView {
    private String currentLocationVillageId;
    private int eatExpenses;

    public MilitaryUnitViewStatic(String id, ENation nation, ECombatUnitMission mission, boolean move, EMilitaryUnitLocation state,
                                  VillageBrief origin, VillageBrief target,
                                  int[] units, String currentLocationVillageId, int eatExpenses) {
        super(id, nation, mission, move, state, origin, target, units);
        this.currentLocationVillageId = currentLocationVillageId;
        this.eatExpenses = eatExpenses;
    }
}
