package io.lanu.travian.game.models.event;

import io.lanu.travian.game.entities.SettlementEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LastEvent implements Event {

    private LocalDateTime executionTime;

    @Override
    public void execute(SettlementEntity entity) {

    }
}
