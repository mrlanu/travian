package io.lanu.travian.game.entities.events;

import io.lanu.travian.game.entities.VillageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class MilitaryUnitStatic extends MilitaryUnit{
    private int eatExpenses;

    @Override
    public void execute(VillageEntity villageEntity) {

    }
}
