package io.lanu.travian.game.models.event;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.ConstructionEventEntity;
import io.lanu.travian.game.repositories.StatisticsRepository;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ConstructionEvent implements Event {

    private final ConstructionEventEntity entity;
    private final StatisticsRepository statisticsRepository;

    @Override
    public void execute(SettlementEntity settlementEntity) {
        var build = settlementEntity.getBuildings().get(this.entity.getBuildingPosition());
        build.setLevel(build.getLevel() + 1);
        settlementEntity.getConstructionEventList().remove(entity);
        var stat = statisticsRepository.findByPlayerId(settlementEntity.getAccountId());
        stat.addPopulation(build.getKind().getCu());
        statisticsRepository.save(stat);
    }

    @Override
    public LocalDateTime getExecutionTime() {
        return entity.getExecutionTime();
    }
}
