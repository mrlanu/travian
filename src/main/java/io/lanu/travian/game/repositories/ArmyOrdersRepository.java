package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.ArmyOrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ArmyOrdersRepository extends MongoRepository<ArmyOrderEntity, String> {
    List<ArmyOrderEntity> findAllByVillageId(String villageId);
    void deleteAllByVillageIdAndEndOrderTimeBefore(String villageId, LocalDateTime before);
}
