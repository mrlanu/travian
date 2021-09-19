package io.lanu.travian.game.controllers;

import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.Event;
import io.lanu.travian.game.models.requests.BuildingRequest;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.VillageView;
import io.lanu.travian.game.services.EventService;
import io.lanu.travian.game.services.VillageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/villages")
public class VillageController {
    private final VillageService villageService;
    private final EventService eventService;

    public VillageController(VillageService villageService, EventService eventService) {
        this.villageService = villageService;
        this.eventService = eventService;
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

    @PostMapping("/{villageId}/buildings/{buildingPosition}/new")
    public ResponseEntity<String> bewBuilding(@PathVariable String villageId,
                                              @PathVariable Integer buildingPosition,
                                              @RequestBody BuildingRequest buildingRequest){
        Event event = eventService.createBuildingNewEvent(villageId, buildingPosition, buildingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(event.getEventId());
    }

    @PutMapping("/{villageId}/fields/{fieldPosition}/upgrade")
    public ResponseEntity<String> upgradeField(@PathVariable String villageId, @PathVariable Integer fieldPosition){
        Event event = eventService.createFieldUpgradeEvent(villageId, fieldPosition);
        return ResponseEntity.status(HttpStatus.OK).body("New Event ID : " + event.getEventId());
    }

}
