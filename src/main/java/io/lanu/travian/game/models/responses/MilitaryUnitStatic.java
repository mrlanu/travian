package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.EMilitaryUnitState;
import io.lanu.travian.enums.ENation;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MilitaryUnitStatic extends MilitaryUnit{
    private String currentLocationVillageId;
    private int eatExpenses;

    public MilitaryUnitStatic(String id, ENation nation, boolean move, EMilitaryUnitState state, VillageBrief homeVillage,
                              int[] units, String currentLocationVillageId, int eatExpenses) {
        super(id, nation, move, state, homeVillage, units);
        this.currentLocationVillageId = currentLocationVillageId;
        this.eatExpenses = eatExpenses;
    }
}
