package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.ResearchedTroopsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResearchedTroopsRepository extends MongoRepository<ResearchedTroopsEntity, String> {
    ResearchedTroopsEntity findByVillageId(String villageId);
}
