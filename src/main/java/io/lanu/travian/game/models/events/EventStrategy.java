package io.lanu.travian.game.models.events;

import io.lanu.travian.game.entities.VillageEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
public abstract class EventStrategy {
    protected VillageEntity origin;
    public abstract void execute();
    public abstract LocalDateTime getExecutionTime();
}
