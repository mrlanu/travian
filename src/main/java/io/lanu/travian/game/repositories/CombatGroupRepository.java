package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.CombatGroupEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CombatGroupRepository extends MongoRepository<CombatGroupEntity, String> {
    List<CombatGroupEntity> getCombatGroupByFromAccountIdOrToAccountIdAndExecutionTimeBefore(String fromId, String toId, LocalDateTime before);
    List<CombatGroupEntity> getCombatGroupByFromSettlementIdOrToSettlementId(String fromId, String toId);
    List<CombatGroupEntity> getAllByToSettlementIdAndMoved(String id, boolean moved);
}
