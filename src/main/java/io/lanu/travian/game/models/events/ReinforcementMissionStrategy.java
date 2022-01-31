package io.lanu.travian.game.models.events;

import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.MilitaryUnitEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.services.MilitaryService;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReinforcementMissionStrategy extends MissionStrategy {
    public ReinforcementMissionStrategy(VillageEntity origin, MovedMilitaryUnitEntity militaryUnit, VillageEntity targetVillage) {
        super(origin, militaryUnit, targetVillage);
    }

    @Override
    void handle(MilitaryService service) {
        System.out.println("Reinforcement has arrived");
        service.deleteMovedUnitById(militaryUnit.getId());
        var unit = new MilitaryUnitEntity(
                militaryUnit.getNation(),
                militaryUnit.getMission(),
                militaryUnit.getUnits(),
                militaryUnit.getOriginVillageId(),
                militaryUnit.getOrigin(),
                militaryUnit.getTargetVillageId(),
                militaryUnit.getTarget(),
                militaryUnit.getEatExpenses()
        );
        service.saveMilitaryUnit(unit);
    }
}
