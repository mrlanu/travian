package io.lanu.travian.game.models.event;

import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.event.missions.AttackMissionStrategy;
import io.lanu.travian.game.models.event.missions.MissionStrategy;
import io.lanu.travian.game.models.event.missions.ReinforcementMissionStrategy;
import io.lanu.travian.game.models.event.missions.ReturnHomeMissionStrategy;
import io.lanu.travian.game.services.EngineService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TroopsArrivedEvent implements Event{
    
    private final CombatGroupEntity combatGroup;
    private final EngineService engineService;

    @Override
    public void execute(SettlementEntity entity) {
        getMissionStrategy(entity).handle();
    }

    @Override
    public LocalDateTime getExecutionTime() {
        return combatGroup.getExecutionTime();
    }
    
    private MissionStrategy getMissionStrategy(SettlementEntity settlementEntity) {
        switch (combatGroup.getMission()){
            case REINFORCEMENT:
                return new ReinforcementMissionStrategy(settlementEntity, combatGroup, engineService);
            case ATTACK:
                return new AttackMissionStrategy(settlementEntity, combatGroup, engineService);
            case BACK:
                return new ReturnHomeMissionStrategy(settlementEntity, combatGroup, engineService);
            default: throw new IllegalStateException();
        }
    }
}
