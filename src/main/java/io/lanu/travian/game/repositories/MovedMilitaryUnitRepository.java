package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovedMilitaryUnitRepository extends MongoRepository<MovedMilitaryUnitEntity, String> {
    List<MovedMilitaryUnitEntity> getAllByOriginVillageId(String villageId);
    List<MovedMilitaryUnitEntity> getAllByOriginVillageIdOrTargetVillageId(String originId, String targetId);
    void deleteById(String id);
}
