package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.events.BuildIEvent;
import io.lanu.travian.game.models.requests.BuildingRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    BuildIEvent createBuildEvent(String villageId, Integer fieldPosition);

    List<BuildIEvent> findAllByVillageId(String villageId);

    void deleteByEventId(String eventId);

    BuildIEvent createBuildingNewEvent(String villageId, Integer buildingPosition, BuildingRequest buildingRequest);

    void deleteAllByVillageIdAndExecutionTimeBefore(String villageId, LocalDateTime time);
}
