package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.ResearchedCombatUnitEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResearchedCombatUnitRepository extends MongoRepository<ResearchedCombatUnitEntity, String> {
    ResearchedCombatUnitEntity findByVillageId(String villageId);
}
