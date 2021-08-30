package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.events.Event;
import io.lanu.travian.game.models.requests.BuildingRequest;

import java.util.List;

public interface EventService {

    Event createFieldUpgradeEvent(String villageId, Integer fieldPosition);

    List<Event> findAllByVillageId(String villageId);

    void deleteByEventId(String eventId);

    Event createBuildingNewEvent(String villageId, Integer buildingPosition, BuildingRequest buildingRequest);
}
