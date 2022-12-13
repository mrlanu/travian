package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.CombatGroupContractEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CombatGroupContractRepository extends MongoRepository<CombatGroupContractEntity, String> {
    void deleteAllByOwnerSettlementId(String settlementId);
}
