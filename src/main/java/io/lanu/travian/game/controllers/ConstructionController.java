package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.EBuilding;
import io.lanu.travian.game.models.responses.NewBuilding;
import io.lanu.travian.game.services.IConstructionService;
import io.lanu.travian.game.services.VillageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villages")
public class ConstructionController {
    private final IConstructionService constructionService;
    private final VillageService villageService;

    public ConstructionController(IConstructionService constructionService, VillageService villageService) {
        this.constructionService = constructionService;
        this.villageService = villageService;
    }

    @GetMapping("/{villageId}/buildings")
    public List<NewBuilding> getListOfAllNewBuildings(@PathVariable String villageId){
        var village = this.villageService.recalculateVillage(villageId);
        return constructionService.getListOfAllNewBuildings(village);
    }

    @PutMapping("/{villageId}/buildings/{position}/new")
    public ResponseEntity<String> newBuilding(@PathVariable String villageId,
                                              @PathVariable Integer position,
                                              @RequestParam EBuilding kind){
        var village = this.villageService.recalculateVillage(villageId);
        village = constructionService.createBuildEvent(village, position, kind);
        villageService.saveVillage(village);
        return ResponseEntity.status(HttpStatus.CREATED).body("Done");
    }

    @PutMapping("/{villageId}/buildings/{position}/upgrade")
    public ResponseEntity<String> upgradeBuilding(@PathVariable String villageId, @PathVariable Integer position){
        var village = this.villageService.recalculateVillage(villageId);
        village = constructionService.createBuildEvent(village, position, null);
        villageService.saveVillage(village);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{villageId}/events/{eventId}")
    public ResponseEntity<String> deleteEventById(@PathVariable String villageId, @PathVariable String eventId){
        var village = this.villageService.recalculateVillage(villageId);
        village = constructionService.deleteBuildingEvent(village, eventId);
        villageService.saveVillage(village);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
