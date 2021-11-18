package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.ResearchedUnitsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResearchedUnitsRepository extends MongoRepository<ResearchedUnitsEntity, String> {
    ResearchedUnitsEntity findByVillageId(String villageId);
}
