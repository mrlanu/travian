package io.lanu.travian.game.models.events;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.models.responses.VillageBrief;
import io.lanu.travian.game.services.SettlementState;
import io.lanu.travian.game.services.MilitaryService;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttackMissionStrategy extends MissionStrategy {

    public AttackMissionStrategy(SettlementEntity origin, MovedMilitaryUnitEntity militaryUnit, VillageBrief targetVillage) {
        super(origin, militaryUnit, targetVillage);
    }

    @Override
    public void handle(SettlementState service, MilitaryService militaryService) {
        System.out.println("the Attack has arrived");
        if (origin.getId().equals(targetVillage.getVillageId())){
            // perform an attack
            System.out.println("A War has begun.... In the village - " + targetVillage.getVillageName());
            militaryService.deleteMovedUnitById(militaryUnit.getId());
            System.out.println("All units have died:(");
        }else{
            service.recalculateCurrentState(targetVillage.getVillageId());
        }
    }
}
