package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EventsType;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.VillageEntityWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DeathEvent extends Event {

    public DeathEvent(LocalDateTime executionTime) {
        super(EventsType.DEATH, executionTime);
    }

    @Override
    public void accept(VillageEntityWrapper villageEntityWrapper) {
        /*Map<UnitType, Integer> army = villageEntity.getArmy().getHomeLegion();
        army.put(UnitType.LEGIONNAIRE, army.getOrDefault(UnitType.LEGIONNAIRE, 0) - 1);
        villageEntity.getWarehouse().addGood(FieldType.CROP, BigDecimal.valueOf(50));
        villageEntity.getProducePerHour().addGood(FieldType.CROP, 10);*/
    }
}
