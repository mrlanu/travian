package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.events.MilitaryEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IMilitaryUnitRepository extends MongoRepository<MilitaryEvent, String> {
    List<MilitaryEvent> getAllByOriginVillageIdOrTargetVillageId(String villageId, String targetVillageId);
    List<MilitaryEvent> getAllByOriginVillageId(String villageId);
}
