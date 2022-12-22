package io.lanu.travian.game.models.event.missions;

import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.services.EngineService;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReinforcementMissionStrategy extends MissionStrategy {
    public ReinforcementMissionStrategy(SettlementEntity currentSettlement, CombatGroupEntity combatGroup, EngineService engineService) {
        super(currentSettlement, combatGroup, engineService);
    }

    @Override
    public void handle() {
        System.out.println("Reinforcement has arrived");
        combatGroup.setMoved(false);
        //combatGroup.setOwnerSettlementId(combatGroup.getToSettlementId());
        engineService.getCombatGroupRepository().save(combatGroup);
    }
}

