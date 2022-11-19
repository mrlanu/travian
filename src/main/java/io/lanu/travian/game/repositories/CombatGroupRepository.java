package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.CombatGroupEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CombatGroupRepository extends MongoRepository<CombatGroupEntity, String> {
    List<CombatGroupEntity> getCombatGroupByOwnerSettlementIdOrToSettlementId(String fromId, String toId);
    List<CombatGroupEntity> getAllByToSettlementIdAndMoved(String id, boolean moved);
}
