package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EUnits;
import io.lanu.travian.game.entities.VillageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

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
        var homeLeg = villageEntity.getHomeLegion();
        homeLeg.put(unitType, homeLeg.getOrDefault(unitType, 0) + 1);
    }
}
