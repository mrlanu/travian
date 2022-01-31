package io.lanu.travian.game.models.events;

import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.services.MilitaryService;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttackMissionStrategy extends MissionStrategy {

    public AttackMissionStrategy(VillageEntity origin, MovedMilitaryUnitEntity militaryUnit, VillageEntity targetVillage) {
        super(origin, militaryUnit, targetVillage);
    }

    @Override
    public void handle(MilitaryService service) {
        System.out.println("Attack has arrived");
    }
}
