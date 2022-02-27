package io.lanu.travian.game.models.events;

import io.lanu.travian.enums.EManipulation;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.models.responses.VillageBrief;
import io.lanu.travian.game.services.MilitaryService;
import io.lanu.travian.game.services.SettlementState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReturnHomeMissionStrategy extends MissionStrategy{

    public ReturnHomeMissionStrategy(SettlementEntity origin, MovedMilitaryUnitEntity militaryUnit, VillageBrief targetVillage) {
        super(origin, militaryUnit, targetVillage);
    }

    @Override
    void handle(SettlementState service, MilitaryService militaryService) {
        // add all returned units to village army
        var homeLegion = origin.getHomeLegion();
        var returnedUnits = militaryUnit.getUnits();
        for (int i = 0; i < homeLegion.length; i++){
            homeLegion[i] = homeLegion[i] + returnedUnits[i];
        }
        //add all plundered resources to storage
        origin.manipulateGoods(EManipulation.ADD, militaryUnit.getPlunder());
        militaryService.deleteMovedUnitById(militaryUnit.getId());
    }
}
