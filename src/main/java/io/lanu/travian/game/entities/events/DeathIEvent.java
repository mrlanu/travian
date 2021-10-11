package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EUnits;
import io.lanu.travian.enums.Resource;
import io.lanu.travian.game.entities.VillageEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
public class DeathIEvent implements IEvent {

    private LocalDateTime executionTime;

    public DeathIEvent(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    @Override
    public void execute(VillageEntity villageEntity) {
        // dummy implementation
        var army = villageEntity.getHomeLegion();
        army.put(EUnits.LEGIONNAIRE, army.getOrDefault(EUnits.LEGIONNAIRE, 0) - 1);
        var stor = villageEntity.getStorage();
        stor.put(Resource.CROP, stor.get(Resource.CROP).add(BigDecimal.ONE));
    }
}
