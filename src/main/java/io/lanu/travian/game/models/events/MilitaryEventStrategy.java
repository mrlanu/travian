package io.lanu.travian.game.models.events;

import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.MilitaryUnitEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MilitaryEventStrategy extends EventStrategy{

    private MilitaryUnitEntity militaryUnit;
    private VillageEntity targetVillage;

    public MilitaryEventStrategy(VillageEntity origin, MilitaryUnitEntity attackingMilitaryUnit, VillageEntity targetVillage) {
        super(origin);
        this.militaryUnit = attackingMilitaryUnit;
        this.targetVillage = targetVillage;
    }

    @Override
    public void execute() {

    }

    @Override
    public LocalDateTime getExecutionTime() {
        return militaryUnit.getExecutionTime();
    }
}
