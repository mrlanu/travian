package io.lanu.travian.game.entities.events;

import io.lanu.travian.game.entities.VillageEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DeathIEvent implements IEvent {

    private LocalDateTime executionTime;

    public DeathIEvent(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    @Override
    public void execute(VillageEntity villageEntity) {
        /*Map<UnitType, Integer> army = villageEntity.getArmy().getHomeLegion();
        army.put(UnitType.LEGIONNAIRE, army.getOrDefault(UnitType.LEGIONNAIRE, 0) - 1);
        villageEntity.getWarehouse().addGood(FieldType.CROP, BigDecimal.valueOf(50));
        villageEntity.getProducePerHour().addGood(FieldType.CROP, 10);*/
    }
}
