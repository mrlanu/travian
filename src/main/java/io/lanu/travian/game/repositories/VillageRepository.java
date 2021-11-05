package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.VillageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VillageRepository extends MongoRepository<VillageEntity, String> {
    List<VillageEntity> findAllByAccountId(String accountId);
}
