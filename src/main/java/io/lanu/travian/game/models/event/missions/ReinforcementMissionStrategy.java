package io.lanu.travian.game.models.event.missions;

import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.services.SettlementState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReinforcementMissionStrategy extends MissionStrategy {
    public ReinforcementMissionStrategy(SettlementEntity currentSettlement, CombatGroupEntity combatGroup, SettlementState settlementState) {
        super(currentSettlement, combatGroup, settlementState);
    }

    @Override
    public void handle() {
        System.out.println("Reinforcement has arrived");
        combatGroup.setMoved(false);
        //combatGroup.setOwnerSettlementId(combatGroup.getToSettlementId());
        settlementState.getCombatGroupRepository().save(combatGroup);
    }
}

