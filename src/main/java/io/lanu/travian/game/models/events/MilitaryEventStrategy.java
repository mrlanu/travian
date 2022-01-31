package io.lanu.travian.game.models.events;

import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.services.MilitaryService;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MilitaryEventStrategy extends EventStrategy{

    private MovedMilitaryUnitEntity militaryUnit;
    private VillageEntity targetVillage;
    private MilitaryService service;

    public MilitaryEventStrategy(VillageEntity origin, MovedMilitaryUnitEntity militaryUnit,
                                 VillageEntity targetVillage, MilitaryService service) {
        super(origin);
        this.militaryUnit = militaryUnit;
        this.targetVillage = targetVillage;
        this.service = service;
    }

    @Override
    public void execute() {
        getMissionStrategy().handle(service);
    }

    private MissionStrategy getMissionStrategy() {
        switch (militaryUnit.getMission()){
            case "Reinforcement":
                return new ReinforcementMissionStrategy(origin, militaryUnit, targetVillage);
            case "Attack":
                return new AttackMissionStrategy(origin, militaryUnit, targetVillage);
            default: throw new IllegalStateException();
        }
    }

    @Override
    public LocalDateTime getExecutionTime() {
        return militaryUnit.getExecutionTime();
    }
}
