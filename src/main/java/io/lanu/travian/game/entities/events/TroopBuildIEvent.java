package io.lanu.travian.game.entities.events;

import io.lanu.travian.game.entities.VillageEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document("events")
@TypeAlias("troopEvent")
public class TroopBuildIEvent implements IEvent {

   /* private UnitType unitType;*/
    private LocalDateTime executionTime;
    private int eatHour;

    public TroopBuildIEvent(LocalDateTime executionTime, int eatHour) {
        this.executionTime = executionTime;
        this.eatHour = eatHour;
    }

    @Override
    public void execute(VillageEntity villageEntity) {
        /*Map<UnitType, Integer> army = villageEntity.getArmy().getHomeLegion();
        army.put(unitType, army.getOrDefault(unitType, 0) + 1);
        villageEntity.getProducePerHour().addGood(FieldType.CROP, -eatHour);*/
    }
}
