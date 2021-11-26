package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.EBuilding;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.ConstructionEvent;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.NewBuilding;
import io.lanu.travian.game.models.responses.VillageView;
import io.lanu.travian.game.services.BuildingService;
import io.lanu.travian.game.services.VillageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/villages")
public class VillageController {
    private final VillageService villageService;
    private final BuildingService buildingService;

    public VillageController(VillageService villageService, BuildingService buildingService) {
        this.villageService = villageService;
        this.buildingService = buildingService;
    }

    @GetMapping("/{villageId}")
    public VillageView getVillageById(@PathVariable String villageId){
        return villageService.getVillageById(villageId);
    }


    @PostMapping("/create-new-village")
    public ResponseEntity<String> newVillage(@RequestBody NewVillageRequest newVillageRequest){
        VillageEntity villageEntity = villageService.createVillage(newVillageRequest);
        return ResponseEntity.status(HttpStatus.OK).body("New Village ID : " + villageEntity.getVillageId());
    }

    @GetMapping("/{villageId}/buildings")
    public List<NewBuilding> getListOfAllNewBuildings(@PathVariable String villageId){
        return buildingService.getListOfAllNewBuildings(villageId);
    }

    @PutMapping("/{villageId}/buildings/{position}/new")
    public ResponseEntity<String> newBuilding(@PathVariable String villageId,
                                              @PathVariable Integer position,
                                              @RequestParam EBuilding kind){
        ConstructionEvent buildEvent = buildingService.createBuildEvent(villageId, position, kind);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildEvent.getEventId());
    }

    @PutMapping("/{villageId}/buildings/{position}/upgrade")
    public ResponseEntity<String> upgradeBuilding(@PathVariable String villageId, @PathVariable Integer position){
        buildingService.createBuildEvent(villageId, position, null);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{villageId}/update-name")
    public ResponseEntity<String> updateName(@PathVariable String villageId, @RequestParam String name){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.villageService.updateName(villageId, name));
    }

    @DeleteMapping("/{villageId}/events/{eventId}")
    public ResponseEntity<String> deleteEventById(@PathVariable String villageId, @PathVariable String eventId){
        this.buildingService.deleteBuildingEvent(villageId, eventId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
