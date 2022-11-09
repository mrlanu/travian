package io.lanu.travian.game.models.event.missions;

import io.lanu.travian.enums.EMilitaryUnitMission;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.models.responses.VillageBrief;
import io.lanu.travian.game.services.SettlementState;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttackMissionStrategy extends MissionStrategy {

    public AttackMissionStrategy(SettlementEntity currentSettlement, MovedMilitaryUnitEntity militaryUnit,
                                 VillageBrief targetVillage, SettlementState settlementState) {
        super(currentSettlement, militaryUnit, targetVillage, settlementState);
    }

    @Override
    public void handle() {
        //here is recursive recalculation of all villages involved in this attack
        // perform an attack if we are in origin village or skip and this attack will be performed in target village during recursion
        if (currentSettlement.getId().equals(targetVillage.getVillageId())){
            var storage = currentSettlement.getStorage();

            //here should be an algorithm of a plunder
            // just dummy implementation
            storage.put(EResource.CLAY, storage.get(EResource.CLAY).subtract(BigDecimal.valueOf(100)));
            militaryUnit.setPlunder(Map.of(EResource.CROP, BigDecimal.ZERO, EResource.CLAY, BigDecimal.valueOf(100),
                    EResource.IRON, BigDecimal.ZERO, EResource.WOOD, BigDecimal.ZERO));
            //----------
            militaryUnit.setMission(EMilitaryUnitMission.BACK.getName());
            militaryUnit.setTargetVillageId(militaryUnit.getOriginVillageId());
            militaryUnit.setOriginVillageId(currentSettlement.getId());
            militaryUnit.setOrigin(militaryUnit.getTarget());
            militaryUnit.setExecutionTime(LocalDateTime.now().plusSeconds(militaryUnit.getDuration()));
            settlementState.getMovedMilitaryUnitRepository().save(militaryUnit);

        } else{
            //just in the skip case
            settlementState.recalculateCurrentState(targetVillage.getVillageId());
        }
    }
}