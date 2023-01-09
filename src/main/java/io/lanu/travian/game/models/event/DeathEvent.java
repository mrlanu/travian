package io.lanu.travian.game.models.event;

import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.entities.SettlementEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class DeathEvent implements Event {

    private LocalDateTime executionTime;

    @Override
    public void execute(SettlementEntity settlementEntity) {
        // dummy implementation
        var army = settlementEntity.getHomeLegion();
        army[0] = army[0] - 1;
        var storage = settlementEntity.getStorage();
        var crop = storage.get(3);
        crop = crop.add(BigDecimal.ONE);
        storage.set(3, crop);
    }

}
