package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.events.BuildIEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    BuildIEvent createBuildEvent(String villageId, Integer fieldPosition);

    List<BuildIEvent> findAllByVillageId(String villageId);

    void deleteByEventId(String eventId);

    void deleteAllByVillageIdAndExecutionTimeBefore(String villageId, LocalDateTime time);
}
