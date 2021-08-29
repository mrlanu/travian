package io.lanu.travian.templates.entities.buildings;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BuildingsRepository extends MongoRepository<BuildingBase, String> {
}
