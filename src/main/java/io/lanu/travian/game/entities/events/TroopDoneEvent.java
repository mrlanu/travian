package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EUnits;
import io.lanu.travian.game.entities.VillageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TroopDoneEvent implements IEvent {

    private LocalDateTime executionTime;
    private EUnits unitType;
    private int eatHour;

    @Override
    public void execute(VillageEntity villageEntity) {
        var homeLeg = villageEntity.getHomeLegion();
        homeLeg.put(unitType, homeLeg.getOrDefault(unitType, 0) + 1);
    }
}
