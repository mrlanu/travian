package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.VillageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VillageRepo extends MongoRepository<VillageEntity, String> {
}
