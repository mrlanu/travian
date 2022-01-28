package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.events.MilitaryUnit;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IMilitaryUnitRepository extends MongoRepository<MilitaryUnit, String> {
    List<MilitaryUnit> getAllByOriginVillageId(String villageId);
    List<MilitaryUnit> getAllByMove(boolean move);
}
