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

    public MilitaryEventStrategy(SettlementEntity origin, MovedMilitaryUnitEntity militaryUnit,
                                 VillageBrief targetVillage, SettlementState service, MilitaryService militaryService) {
        super(origin);
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
                return new ReinforcementMissionStrategy(origin, militaryUnit, targetVillage);
            case "Attack":
                return new AttackMissionStrategy(origin, militaryUnit, targetVillage);
            case "Return to home":
                return new ReturnHomeMissionStrategy(origin, militaryUnit, targetVillage);
            default: throw new IllegalStateException();
        }
    }

    @Override
    public LocalDateTime getExecutionTime() {
        return militaryUnit.getExecutionTime();
    }
}
