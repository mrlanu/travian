package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.ECombatUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombatUnitDoneEventEntity {

    private LocalDateTime executionTime;
    private ECombatUnit unitType;
    private int eatHour;
}
