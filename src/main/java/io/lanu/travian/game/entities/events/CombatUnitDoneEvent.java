package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.game.entities.VillageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombatUnitDoneEvent implements IEvent {

    private LocalDateTime executionTime;
    private ECombatUnit unitType;
    private int eatHour;

    @Override
    public void execute(VillageEntity villageEntity) {
        var homeLeg = villageEntity.getHomeLegion();
        homeLeg[0] = homeLeg[0] + 1;
    }
}
