package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.events.MilitaryUnitEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IMilitaryUnitRepository extends MongoRepository<MilitaryUnitEntity, String> {
    List<MilitaryUnitEntity> getAllByOriginVillageId(String villageId);
    List<MilitaryUnitEntity> getAllByMove(boolean move);
}
