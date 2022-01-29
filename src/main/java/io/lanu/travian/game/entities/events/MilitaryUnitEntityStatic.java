package io.lanu.travian.game.entities.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class MilitaryUnitEntityStatic extends MilitaryUnitEntity {
    private int eatExpenses;
}
