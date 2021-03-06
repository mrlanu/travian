package io.lanu.travian.game.models.events;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.CombatUnitDoneEventEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class CombatUnitDoneStrategy extends EventStrategy{

    private CombatUnitDoneEventEntity entity;

    public CombatUnitDoneStrategy(SettlementEntity currentSettlement, CombatUnitDoneEventEntity entity) {
        super(currentSettlement);
        this.entity = entity;
    }

    @Override
    public void execute() {
        var homeLeg = currentSettlement.getHomeLegion();
        homeLeg[0] = homeLeg[0] + 1;
    }

    @Override
    public LocalDateTime getExecutionTime() {
        return entity.getExecutionTime();
    }
}
