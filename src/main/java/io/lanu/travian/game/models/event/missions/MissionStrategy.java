package io.lanu.travian.game.models.event.missions;

import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.services.EngineService;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class MissionStrategy {
    protected SettlementEntity currentSettlement;
    protected CombatGroupEntity combatGroup;
    protected EngineService engineService;

    public MissionStrategy(SettlementEntity currentSettlement,
                           CombatGroupEntity combatGroup,
                           EngineService engineService) {
        this.currentSettlement = currentSettlement;
        this.combatGroup = combatGroup;
        this.engineService = engineService;
    }

    public abstract void handle();
}
