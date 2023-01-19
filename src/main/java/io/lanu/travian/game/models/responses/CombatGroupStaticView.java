package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.ECombatGroupLocation;
import io.lanu.travian.enums.ECombatGroupMission;
import io.lanu.travian.enums.ENation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CombatGroupStaticView extends CombatGroupView {
    private String currentLocationVillageId;
    private int eatExpenses;

    public CombatGroupStaticView(String id, ENation nation, ECombatGroupMission mission, boolean move, ECombatGroupLocation state,
                                 VillageBrief from, VillageBrief to,
                                 List<Integer> units, String currentLocationVillageId, int eatExpenses) {
        super(id, nation, mission, move, state, from, to, units);
        this.currentLocationVillageId = currentLocationVillageId;
        this.eatExpenses = eatExpenses;
    }
}
