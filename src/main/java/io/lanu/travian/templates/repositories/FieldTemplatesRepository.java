package io.lanu.travian.templates.repositories;

import io.lanu.travian.enums.Resource;
import io.lanu.travian.templates.entities.FieldTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FieldTemplatesRepository extends MongoRepository<FieldTemplate, String> {
    FieldTemplate findByFieldTypeAndLevel(Resource fieldType, int level);
}
