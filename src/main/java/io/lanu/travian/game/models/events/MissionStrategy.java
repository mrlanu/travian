package io.lanu.travian.game.models.events;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.models.responses.VillageBrief;
import io.lanu.travian.game.services.SettlementState;
import io.lanu.travian.game.services.MilitaryService;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class MissionStrategy {
    protected SettlementEntity currentSettlement;
    protected MovedMilitaryUnitEntity militaryUnit;
    protected VillageBrief targetVillage;

    public MissionStrategy(SettlementEntity currentSettlement, MovedMilitaryUnitEntity militaryUnit, VillageBrief targetVillage) {
        this.currentSettlement = currentSettlement;
        this.militaryUnit = militaryUnit;
        this.targetVillage = targetVillage;
    }

    abstract void handle(SettlementState service, MilitaryService militaryService);
}
