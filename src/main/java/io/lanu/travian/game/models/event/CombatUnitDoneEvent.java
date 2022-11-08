package io.lanu.travian.game.models.event;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.CombatUnitDoneEventEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CombatUnitDoneEvent implements Event{

    private final CombatUnitDoneEventEntity entity;

    @Override
    public void execute(SettlementEntity settlementEntity) {
        var homeLeg = settlementEntity.getHomeLegion();
        homeLeg[0] = homeLeg[0] + 1;
    }

    @Override
    public LocalDateTime getExecutionTime() {
        return entity.getExecutionTime();
    }
}
