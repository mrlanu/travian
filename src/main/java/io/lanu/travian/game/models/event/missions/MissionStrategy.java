package io.lanu.travian.game.models.event.missions;

import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.services.SettlementState;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class MissionStrategy {
    protected SettlementEntity currentSettlement;
    protected CombatGroupEntity combatGroup;
    protected SettlementState settlementState;

    public MissionStrategy(SettlementEntity currentSettlement,
                           CombatGroupEntity combatGroup,
                           SettlementState settlementState) {
        this.currentSettlement = currentSettlement;
        this.combatGroup = combatGroup;
        this.settlementState = settlementState;
    }

    public abstract void handle();
}
