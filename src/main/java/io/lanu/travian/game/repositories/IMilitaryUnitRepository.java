package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.MilitaryUnitEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IMilitaryUnitRepository extends MongoRepository<MilitaryUnitEntity, String> {
}
