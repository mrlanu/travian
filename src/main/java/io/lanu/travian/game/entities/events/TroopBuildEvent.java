package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EUnits;
import io.lanu.travian.game.entities.VillageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("events")
@TypeAlias("troopEvent")
public class TroopBuildEvent implements IEvent {

    private LocalDateTime executionTime;
    private EUnits unitType;
    private int eatHour;

    @Override
    public void execute(VillageEntity villageEntity) {
        /*Map<UnitType, Integer> army = villageEntity.getArmy().getHomeLegion();
        army.put(unitType, army.getOrDefault(unitType, 0) + 1);
        villageEntity.getProducePerHour().addGood(FieldType.CROP, -eatHour);*/
    }
}
