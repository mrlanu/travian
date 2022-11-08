package io.lanu.travian.game.models.event.missions;

import io.lanu.travian.enums.EManipulation;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.models.responses.VillageBrief;
import io.lanu.travian.game.services.SettlementState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReturnHomeMissionStrategy extends MissionStrategy{

    public ReturnHomeMissionStrategy(SettlementEntity currentSettlement, MovedMilitaryUnitEntity militaryUnit,
                                     VillageBrief targetVillage, SettlementState settlementState) {
        super(currentSettlement, militaryUnit, targetVillage, settlementState);
    }

    @Override
    public void handle() {
        // add all returned units to village army
        var homeLegion = currentSettlement.getHomeLegion();
        var returnedUnits = militaryUnit.getUnits();
        for (int i = 0; i < homeLegion.length; i++){
            homeLegion[i] = homeLegion[i] + returnedUnits[i];
        }
        //add all plundered resources to storage
        currentSettlement.manipulateGoods(EManipulation.ADD, militaryUnit.getPlunder());
        settlementState.getMovedMilitaryUnitRepository().deleteById(militaryUnit.getId());
    }
}
