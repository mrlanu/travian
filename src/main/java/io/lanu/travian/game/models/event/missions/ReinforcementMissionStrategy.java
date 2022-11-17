package io.lanu.travian.game.models.event.missions;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.MilitaryUnitEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.models.responses.VillageBrief;
import io.lanu.travian.game.services.SettlementState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReinforcementMissionStrategy extends MissionStrategy {
    public ReinforcementMissionStrategy(SettlementEntity currentSettlement, MovedMilitaryUnitEntity militaryUnit,
                                        VillageBrief targetVillage, SettlementState settlementState) {
        super(currentSettlement, militaryUnit, targetVillage, settlementState);
    }

    @Override
    public void handle() {
        System.out.println("Reinforcement has arrived");
        settlementState.getMovedMilitaryUnitRepository().deleteById(militaryUnit.getId());
        var unit = new MilitaryUnitEntity(
                militaryUnit.getNation(),
                militaryUnit.getMission(),
                militaryUnit.getUnits(),
                militaryUnit.getOrigin().getVillageId(),
                militaryUnit.getOrigin(),
                militaryUnit.getTarget().getVillageId(),
                militaryUnit.getTarget(),
                militaryUnit.getEatExpenses()
        );
        settlementState.getMilitaryUnitRepository().save(unit);
    }
}
