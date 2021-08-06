package io.lanu.travian.templates.repositories;

import io.lanu.travian.enums.VillageType;
import io.lanu.travian.templates.entities.VillageTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VillageTemplatesRepo extends MongoRepository<VillageTemplate, String> {
    VillageTemplate findByVillageType(VillageType villageType);
}
