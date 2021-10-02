package io.lanu.travian.game.entities.events;

import io.lanu.travian.game.entities.VillageEntity;

import java.time.LocalDateTime;

public interface IEvent {
    void execute(VillageEntity villageEntity);
    LocalDateTime getExecutionTime();
}
