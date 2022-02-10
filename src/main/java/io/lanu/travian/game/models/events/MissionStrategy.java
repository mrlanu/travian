package io.lanu.travian.game.models.events;

import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.models.responses.VillageBrief;
import io.lanu.travian.game.services.IState;
import io.lanu.travian.game.services.MilitaryService;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class MissionStrategy {
    protected VillageEntity origin;
    protected MovedMilitaryUnitEntity militaryUnit;
    protected VillageBrief targetVillage;

    public MissionStrategy(VillageEntity origin, MovedMilitaryUnitEntity militaryUnit, VillageBrief targetVillage) {
        this.origin = origin;
        this.militaryUnit = militaryUnit;
        this.targetVillage = targetVillage;
    }

    abstract void handle(IState service, MilitaryService militaryService);
}