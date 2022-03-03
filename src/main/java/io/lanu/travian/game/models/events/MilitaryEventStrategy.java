package io.lanu.travian.game.models.events;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.models.responses.VillageBrief;
import io.lanu.travian.game.services.SettlementState;
import io.lanu.travian.game.services.MilitaryService;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MilitaryEventStrategy extends EventStrategy{

    private MovedMilitaryUnitEntity militaryUnit;
    private VillageBrief targetVillage;
    private final SettlementState service;
    private final MilitaryService militaryService;

    public MilitaryEventStrategy(SettlementEntity currentSettlement, MovedMilitaryUnitEntity militaryUnit,
                                 VillageBrief targetVillage, SettlementState service, MilitaryService militaryService) {
        super(currentSettlement);
        this.militaryUnit = militaryUnit;
        this.targetVillage = targetVillage;
        this.service = service;
        this.militaryService = militaryService;
    }

    @Override
    public void execute() {
        getMissionStrategy().handle(service, militaryService);
    }

    private MissionStrategy getMissionStrategy() {
        switch (militaryUnit.getMission()){
            case "Reinforcement":
                return new ReinforcementMissionStrategy(currentSettlement, militaryUnit, targetVillage);
            case "Attack":
                return new AttackMissionStrategy(currentSettlement, militaryUnit, targetVillage);
            case "Return to home":
                return new ReturnHomeMissionStrategy(currentSettlement, militaryUnit, targetVillage);
            default: throw new IllegalStateException();
        }
    }

    @Override
    public LocalDateTime getExecutionTime() {
        return militaryUnit.getExecutionTime();
    }
}
