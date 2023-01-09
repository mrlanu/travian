package io.lanu.travian.game.entities.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombatUnitDoneEventEntity {

    private LocalDateTime executionTime;
    private int unit;
}
