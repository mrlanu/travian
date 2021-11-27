package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.entities.VillageEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DeathEvent implements IEvent {

    private LocalDateTime executionTime;

    public DeathEvent(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    @Override
    public void execute(VillageEntity villageEntity) {
        // dummy implementation
        var army = villageEntity.getHomeLegion();
        army.put(ECombatUnit.PHALANX, army.getOrDefault(ECombatUnit.PHALANX, 0) - 1);
        var stor = villageEntity.getStorage();
        stor.put(EResource.CROP, stor.get(EResource.CROP).add(BigDecimal.ONE));
    }
}
