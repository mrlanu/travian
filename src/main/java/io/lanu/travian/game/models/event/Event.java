package io.lanu.travian.game.models.event;

import io.lanu.travian.game.dto.SettlementStateDTO;

import java.time.LocalDateTime;

public interface Event {
    void execute(SettlementStateDTO state);
    LocalDateTime getExecutionTime();
}
