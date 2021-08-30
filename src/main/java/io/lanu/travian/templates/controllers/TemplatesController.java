package io.lanu.travian.templates.controllers;

import io.lanu.travian.templates.entities.FieldTemplate;
import io.lanu.travian.templates.entities.VillageTemplate;
import io.lanu.travian.templates.repositories.BuildingsRepository;
import io.lanu.travian.templates.entities.buildings.GranaryBuilding;
import io.lanu.travian.templates.repositories.FieldTemplatesRepository;
import io.lanu.travian.templates.repositories.VillageTemplatesRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/templates")
public class TemplatesController {

    private final VillageTemplatesRepo villageTemplatesRepo;
    private final FieldTemplatesRepository fieldTemplatesRepository;
    private final BuildingsRepository buildingsRepository;

    public TemplatesController(VillageTemplatesRepo villageTemplatesRepo,
                               FieldTemplatesRepository fieldTemplatesRepository,
                               BuildingsRepository buildingsRepository) {
        this.villageTemplatesRepo = villageTemplatesRepo;
        this.fieldTemplatesRepository = fieldTemplatesRepository;
        this.buildingsRepository = buildingsRepository;
    }

    @PostMapping("/villages/new")
    public ResponseEntity<String> createVillage(@RequestBody VillageTemplate villageTemplate){
        villageTemplate.sumProducePerHour();
        villageTemplatesRepo.save(villageTemplate);
        return ResponseEntity.status(HttpStatus.OK).body("New Village template has been created");
    }

    @PostMapping("/buildings/new")
    public ResponseEntity<GranaryBuilding> createBuilding(@RequestBody GranaryBuilding granaryBuilding){
        var building = buildingsRepository.save(granaryBuilding);
        return ResponseEntity.status(HttpStatus.CREATED).body(building);
    }

    @PostMapping("/fields/new")
    public ResponseEntity<String> createField(@RequestBody FieldTemplate fieldTemplate){
        fieldTemplatesRepository.save(fieldTemplate);
        return ResponseEntity.status(HttpStatus.OK).body("New Field template has been created");
    }
}
