package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.StatisticsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatisticsRepository extends MongoRepository<StatisticsEntity, String> {
}
