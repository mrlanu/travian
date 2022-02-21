package io.lanu.travian.game.models.events;

import io.lanu.travian.game.entities.SettlementEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
public abstract class EventStrategy {
    protected SettlementEntity origin;
    public abstract void execute();
    public abstract LocalDateTime getExecutionTime();
}
