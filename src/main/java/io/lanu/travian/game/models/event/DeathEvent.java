package io.lanu.travian.game.models.event;

import io.lanu.travian.game.dto.SettlementStateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class DeathEvent implements Event {

    private LocalDateTime executionTime;

    @Override
    public void execute(SettlementStateDTO state) {
        // dummy implementation
        var army = state.getSettlementEntity().getHomeLegion();
        army.set(0, army.get(0) - 1);
        var storage = state.getSettlementEntity().getStorage();
        var crop = storage.get(3);
        crop = crop.add(BigDecimal.ONE);
        storage.set(3, crop);
    }

}
