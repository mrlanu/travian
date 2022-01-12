package io.lanu.travian.game.services;

import io.lanu.travian.enums.EBuilding;
import io.lanu.travian.game.entities.events.ConstructionEvent;
import io.lanu.travian.game.models.responses.NewBuilding;

import java.time.LocalDateTime;
import java.util.List;

public interface BuildingService {

    ConstructionEvent createBuildEvent(String villageId, Integer fieldPosition, EBuilding kind);

    List<ConstructionEvent> findAllByVillageId(String villageId);

    void deleteBuildingEvent(String villageId, String eventId);

    void deleteAllByVillageIdAndExecutionTimeBefore(String villageId, LocalDateTime time);

    List<NewBuilding> getListOfAllNewBuildings(String villageId);
}