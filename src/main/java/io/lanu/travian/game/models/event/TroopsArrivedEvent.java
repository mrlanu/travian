package io.lanu.travian.game.models.event;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.models.event.missions.AttackMissionStrategy;
import io.lanu.travian.game.models.event.missions.MissionStrategy;
import io.lanu.travian.game.models.event.missions.ReinforcementMissionStrategy;
import io.lanu.travian.game.models.event.missions.ReturnHomeMissionStrategy;
import io.lanu.travian.game.models.responses.VillageBrief;
import io.lanu.travian.game.services.SettlementState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TroopsArrivedEvent implements Event{
    
    private final MovedMilitaryUnitEntity militaryUnit;
    private final VillageBrief targetVillage;
    private final SettlementState settlementState;
    
    @Override
    public void execute(SettlementEntity entity) {
        getMissionStrategy(entity).handle();
    }

    @Override
    public LocalDateTime getExecutionTime() {
        return militaryUnit.getExecutionTime();
    }
    
    private MissionStrategy getMissionStrategy(SettlementEntity settlementEntity) {
        switch (militaryUnit.getMission()){
            case "Reinforcement":
                return new ReinforcementMissionStrategy(settlementEntity, militaryUnit, targetVillage, settlementState);
            case "Attack":
                return new AttackMissionStrategy(settlementEntity, militaryUnit, targetVillage, settlementState);
            case "Return to home":
                return new ReturnHomeMissionStrategy(settlementEntity, militaryUnit, targetVillage, settlementState);
            default: throw new IllegalStateException();
        }
    }
}
