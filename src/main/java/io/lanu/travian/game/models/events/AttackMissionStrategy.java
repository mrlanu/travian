package io.lanu.travian.game.models.events;

import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.MilitaryUnitEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttackMissionStrategy extends MissionStrategy {

    public AttackMissionStrategy(VillageEntity origin, MilitaryUnitEntity militaryUnit, VillageEntity targetVillage) {
        super(origin, militaryUnit, targetVillage);
    }

    @Override
    void handle() {
        System.out.println("Attack has arrived");
    }
}
