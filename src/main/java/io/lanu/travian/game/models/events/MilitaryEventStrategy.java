package io.lanu.travian.game.models.events;

import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.MilitaryUnitEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MilitaryEventStrategy extends EventStrategy{

    private MilitaryUnitEntity militaryUnit;
    private VillageEntity targetVillage;

    public MilitaryEventStrategy(VillageEntity origin, MilitaryUnitEntity militaryUnit, VillageEntity targetVillage) {
        super(origin);
        this.militaryUnit = militaryUnit;
        this.targetVillage = targetVillage;
    }

    @Override
    public void execute() {
        getMissionStrategy().handle();
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
