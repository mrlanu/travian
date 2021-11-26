package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CombatUnitOrderRepository extends MongoRepository<OrderCombatUnitEntity, String> {
    List<OrderCombatUnitEntity> findAllByVillageId(String villageId);
    void deleteAllByVillageIdAndEndOrderTimeBefore(String villageId, LocalDateTime before);
}
