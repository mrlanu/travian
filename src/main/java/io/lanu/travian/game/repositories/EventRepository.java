package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.events.BuildIEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends MongoRepository<BuildIEvent, String> {
    List<BuildIEvent> findAllByVillageId(String villageId);
    void deleteByEventId(String eventId);
    void deleteAllByVillageIdAndExecutionTimeBefore(String villageId, LocalDateTime time);
}
