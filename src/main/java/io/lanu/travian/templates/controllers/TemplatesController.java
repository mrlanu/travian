package io.lanu.travian.templates.controllers;

import io.lanu.travian.templates.entities.FieldTemplate;
import io.lanu.travian.templates.entities.VillageTemplate;
import io.lanu.travian.templates.repositories.FieldTemplatesRepository;
import io.lanu.travian.templates.repositories.VillageTemplatesRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/templates")
public class TemplatesController {

    private final VillageTemplatesRepo villageTemplatesRepo;
    private final FieldTemplatesRepository fieldTemplatesRepository;

    public TemplatesController(VillageTemplatesRepo villageTemplatesRepo, FieldTemplatesRepository fieldTemplatesRepository) {
        this.villageTemplatesRepo = villageTemplatesRepo;
        this.fieldTemplatesRepository = fieldTemplatesRepository;
    }

    @PostMapping("/create-village")
    public ResponseEntity<String> createVillage(@RequestBody VillageTemplate villageTemplate){
        villageTemplate.sumProducePerHour();
        villageTemplatesRepo.save(villageTemplate);
        return ResponseEntity.status(HttpStatus.OK).body("New Village template has been created");
    }

    @PostMapping("/create-field")
    public ResponseEntity<String> createField(@RequestBody FieldTemplate fieldTemplate){
        fieldTemplatesRepository.save(fieldTemplate);
        return ResponseEntity.status(HttpStatus.OK).body("New Field template has been created");
    }
}
