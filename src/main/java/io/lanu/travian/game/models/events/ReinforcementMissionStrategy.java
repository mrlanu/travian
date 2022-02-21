package io.lanu.travian.game.models.events;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.MilitaryUnitEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.models.responses.VillageBrief;
import io.lanu.travian.game.services.SettlementState;
import io.lanu.travian.game.services.MilitaryService;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReinforcementMissionStrategy extends MissionStrategy {
    public ReinforcementMissionStrategy(SettlementEntity origin, MovedMilitaryUnitEntity militaryUnit, VillageBrief targetVillage) {
        super(origin, militaryUnit, targetVillage);
    }

    @Override
    void handle(SettlementState service, MilitaryService militaryService) {
        System.out.println("Reinforcement has arrived");
        militaryService.deleteMovedUnitById(militaryUnit.getId());
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
        militaryService.saveMilitaryUnit(unit);
    }
}
