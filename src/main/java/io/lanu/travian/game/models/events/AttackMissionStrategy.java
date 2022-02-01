package io.lanu.travian.game.models.events;

import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.models.responses.VillageBrief;
import io.lanu.travian.game.services.IState;
import io.lanu.travian.game.services.MilitaryService;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttackMissionStrategy extends MissionStrategy {

    public AttackMissionStrategy(VillageEntity origin, MovedMilitaryUnitEntity militaryUnit, VillageBrief targetVillage) {
        super(origin, militaryUnit, targetVillage);
    }

    @Override
    public void handle(IState service, MilitaryService militaryService) {
        System.out.println("the Attack has arrived");
        if (origin.getVillageId().equals(targetVillage.getVillageId())){
            // perform an attack
            System.out.println("A War has begun.... In the village - " + targetVillage.getVillageName());
            militaryService.deleteMovedUnitById(militaryUnit.getId());
            System.out.println("All units have died:(");
        }else{
            service.recalculateCurrentState(targetVillage.getVillageId());
        }
    }
}
