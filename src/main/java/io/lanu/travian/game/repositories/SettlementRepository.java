package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.SettlementEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends MongoRepository<SettlementEntity, String> {
    List<SettlementEntity> findAllByAccountId(String accountId);
    Optional<SettlementEntity> findByXAndY(int x, int y);
}
