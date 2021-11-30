package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.VillageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VillageRepository extends MongoRepository<VillageEntity, String> {
    List<VillageEntity> findAllByAccountId(String accountId);
    Optional<VillageEntity> findByXAndY(int x, int y);
}
