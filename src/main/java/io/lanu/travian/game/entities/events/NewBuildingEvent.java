package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EventsType;
import io.lanu.travian.game.models.VillageEntityWrapper;
import io.lanu.travian.templates.buildings.BuildingBase;

import java.time.LocalDateTime;

public class NewBuildingEvent extends Event{

    private BuildingBase building;

    public NewBuildingEvent(String villageId, LocalDateTime executionTime, BuildingBase building) {
        super(EventsType.NEW_BUILDING, villageId, executionTime);
        this.building = building;
    }

    @Override
    public void accept(VillageEntityWrapper villageEntityWrapper) {
        villageEntityWrapper.getVillageEntity().getBuildings().put(building.getPosition(), this.building);
    }
}
