package io.lanu.travian.game.models.event;

import io.lanu.travian.game.entities.SettlementEntity;

import java.time.LocalDateTime;

public interface Event {
    void execute(SettlementEntity entity);
    LocalDateTime getExecutionTime();
}
