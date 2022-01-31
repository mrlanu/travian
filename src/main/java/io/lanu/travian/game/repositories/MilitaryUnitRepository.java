package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.events.MilitaryUnitEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MilitaryUnitRepository extends MongoRepository<MilitaryUnitEntity, String> {
    List<MilitaryUnitEntity> getAllByOriginVillageId(String villageId);
    List<MilitaryUnitEntity> getAllByTargetVillageId(String villageId);
    List<MilitaryUnitEntity> getAllByOriginVillageIdOrTargetVillageId(String originId, String targetId);
    void deleteById(String id);
}
