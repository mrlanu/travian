package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.events.ConstructionEventEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConstructionEventRepository extends MongoRepository<ConstructionEventEntity, String> {
    List<ConstructionEventEntity> findAllByVillageId(String villageId);
    ConstructionEventEntity findBuildIEventByEventId(String eventId);
    void deleteByEventId(String eventId);
    void deleteAllByVillageIdAndExecutionTimeBefore(String villageId, LocalDateTime time);
}
