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
        // will be updated while recalculating attack or raid
        var combatGroupUpdated = engineService
                .getCombatGroupRepository().findById(combatGroup.getId()).orElseThrow();
        currentSettlement.manipulateHomeLegion(combatGroupUpdated.getUnits());
        //add all plundered resources to storage
        currentSettlement.manipulateGoods(EManipulation.ADD, combatGroupUpdated.getPlunder());
        engineService.getCombatGroupRepository().deleteById(combatGroupUpdated.getId());
    }
}
