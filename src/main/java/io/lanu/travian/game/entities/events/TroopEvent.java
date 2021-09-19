package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EventsType;
import io.lanu.travian.game.models.VillageManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Document("events")
@TypeAlias("troopEvent")
public class TroopEvent extends Event {

   /* private UnitType unitType;*/
    private int eatHour;

    public TroopEvent(LocalDateTime executionTime, String villageId, int eatHour) {
        super(EventsType.TROOP, villageId, executionTime);
        this.eatHour = eatHour;
    }

    @Override
    public void accept(VillageManager villageManager) {
        /*Map<UnitType, Integer> army = villageEntity.getArmy().getHomeLegion();
        army.put(unitType, army.getOrDefault(unitType, 0) + 1);
        villageEntity.getProducePerHour().addGood(FieldType.CROP, -eatHour);*/
    }
}
