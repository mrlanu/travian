package io.lanu.travian.game.models.events;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.ConstructionEventEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class ConstructionEventStrategy extends EventStrategy {

    private ConstructionEventEntity entity;

    public ConstructionEventStrategy(SettlementEntity currentSettlement, ConstructionEventEntity entity) {
        super(currentSettlement);
        this.entity = entity;
    }

    @Override
    public void execute() {
        var build = currentSettlement.getBuildings().get(entity.getBuildingPosition());
        build.setLevel(build.getLevel() + 1);
    }

    @Override
    public LocalDateTime getExecutionTime() {
        return entity.getExecutionTime();
    }
}
