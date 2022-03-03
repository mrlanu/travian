package io.lanu.travian.game.models.events;

import io.lanu.travian.enums.EResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DeathEventStrategy extends EventStrategy{

    private LocalDateTime executionTime;


    public DeathEventStrategy(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    @Override
    public void execute() {
        // dummy implementation
        var army = currentSettlement.getHomeLegion();
        army[0] = army[0] - 1;
        var stor = currentSettlement.getStorage();
        stor.put(EResource.CROP, stor.get(EResource.CROP).add(BigDecimal.ONE));
    }
}
