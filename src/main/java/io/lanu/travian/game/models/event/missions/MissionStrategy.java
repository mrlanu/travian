package io.lanu.travian.game.models.event.missions;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.models.responses.VillageBrief;
import io.lanu.travian.game.services.SettlementState;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class MissionStrategy {
    protected SettlementEntity currentSettlement;
    protected MovedMilitaryUnitEntity militaryUnit;
    protected VillageBrief targetVillage;
    protected SettlementState settlementState;

    public MissionStrategy(SettlementEntity currentSettlement,
                           MovedMilitaryUnitEntity militaryUnit,
                           VillageBrief targetVillage,
                           SettlementState settlementState) {
        this.currentSettlement = currentSettlement;
        this.militaryUnit = militaryUnit;
        this.targetVillage = targetVillage;
        this.settlementState = settlementState;
    }

    public abstract void handle();
}
