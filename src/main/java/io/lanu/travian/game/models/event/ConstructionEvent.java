package io.lanu.travian.game.models.event;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.ConstructionEventEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ConstructionEvent implements Event {

    private final ConstructionEventEntity entity;

    @Override
    public void execute(SettlementEntity settlementEntity) {
        var build = settlementEntity.getBuildings().get(this.entity.getBuildingPosition());
        build.setLevel(build.getLevel() + 1);
    }

    @Override
    public LocalDateTime getExecutionTime() {
        return entity.getExecutionTime();
    }
}
