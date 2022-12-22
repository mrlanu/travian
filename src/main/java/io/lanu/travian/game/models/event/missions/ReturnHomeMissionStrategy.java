package io.lanu.travian.game.models.event.missions;

import io.lanu.travian.enums.EManipulation;
import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.services.EngineService;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReturnHomeMissionStrategy extends MissionStrategy{

    public ReturnHomeMissionStrategy(SettlementEntity currentSettlement, CombatGroupEntity combatGroup, EngineService engineService) {
        super(currentSettlement, combatGroup, engineService);
    }

    @Override
    public void handle() {
        // add all returned units to village army
        var homeLegion = currentSettlement.getHomeLegion();
        var returnedUnits = combatGroup.getUnits();
        for (int i = 0; i < homeLegion.length; i++){
            homeLegion[i] = homeLegion[i] + returnedUnits[i];
        }
        //add all plundered resources to storage
        currentSettlement.manipulateGoods(EManipulation.ADD, combatGroup.getPlunder());
        engineService.getCombatGroupRepository().deleteById(combatGroup.getId());
    }
}
