package io.lanu.travian.game.models.event;

import io.lanu.travian.game.dto.SettlementStateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LastEvent implements Event {

    private LocalDateTime executionTime;

    @Override
    public void execute(SettlementStateDTO state) {

    }
}
