package io.lanu.travian.game.models.event.missions;

import io.lanu.travian.enums.EManipulation;
import io.lanu.travian.game.dto.SettlementStateDTO;
import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.services.EngineService;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReturnHomeMissionStrategy extends MissionStrategy{

    public ReturnHomeMissionStrategy(SettlementStateDTO state, CombatGroupEntity combatGroup, EngineService engineService) {
        super(state, combatGroup, engineService);
    }

    @Override
    public void handle() {
        // add all returned units to village army
        // will be updated while recalculating attack or raid
        var combatGroupUpdated = engineService
                .getCombatGroupRepository().findById(combatGroup.getId());
        if (combatGroupUpdated.isPresent()){
            state.getSettlementEntity().manipulateHomeLegion(combatGroupUpdated.get().getUnits());
            //add all plundered resources to storage
            state.getSettlementEntity().manipulateGoods(EManipulation.ADD, combatGroupUpdated.get().getPlunder());
            engineService.getCombatGroupRepository().deleteById(combatGroupUpdated.get().getId());
        }
    }
}
