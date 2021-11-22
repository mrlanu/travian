package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.events.ConstructionEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConstructionEventsRepository extends MongoRepository<ConstructionEvent, String> {
    List<ConstructionEvent> findAllByVillageId(String villageId);
    ConstructionEvent findBuildIEventByEventId(String eventId);
    void deleteByEventId(String eventId);
    void deleteAllByVillageIdAndExecutionTimeBefore(String villageId, LocalDateTime time);
}
