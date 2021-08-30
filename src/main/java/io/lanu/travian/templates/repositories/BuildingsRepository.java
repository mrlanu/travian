package io.lanu.travian.templates.repositories;

import io.lanu.travian.enums.BuildingType;
import io.lanu.travian.templates.entities.buildings.BuildingBase;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BuildingsRepository extends MongoRepository<BuildingBase, String> {
    Optional<BuildingBase> findBuildingBaseByBuildingTypeAndLevel(BuildingType buildingType, Integer level);
}
