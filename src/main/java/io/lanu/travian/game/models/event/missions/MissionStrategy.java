package io.lanu.travian.game.models.event.missions;

import io.lanu.travian.game.dto.SettlementStateDTO;
import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.services.EngineService;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class MissionStrategy {
    protected SettlementStateDTO state;
    protected CombatGroupEntity combatGroup;
    protected EngineService engineService;

    public MissionStrategy(SettlementStateDTO state,
                           CombatGroupEntity combatGroup,
                           EngineService engineService) {
        this.state = state;
        this.combatGroup = combatGroup;
        this.engineService = engineService;
    }

    public abstract void handle();
}
