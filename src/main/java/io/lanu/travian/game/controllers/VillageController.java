package io.lanu.travian.game.controllers;

import io.lanu.travian.game.entities.ArmyOrderEntity;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.BuildIEvent;
import io.lanu.travian.game.models.requests.ArmyOrderRequest;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.VillageView;
import io.lanu.travian.game.services.ArmiesService;
import io.lanu.travian.game.services.EventService;
import io.lanu.travian.game.services.VillageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/villages")
public class VillageController {
    private final VillageService villageService;
    private final EventService eventService;
    private final ArmiesService armiesService;

    public VillageController(VillageService villageService, EventService eventService, ArmiesService armiesService) {
        this.villageService = villageService;
        this.eventService = eventService;
        this.armiesService = armiesService;
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
                                              @PathVariable Integer buildingPosition){
        BuildIEvent buildEvent = eventService.createBuildEvent(villageId, buildingPosition);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildEvent.getEventId());
    }

    @PutMapping("/{villageId}/fields/{fieldPosition}/upgrade")
    public ResponseEntity<String> upgradeField(@PathVariable String villageId, @PathVariable Integer fieldPosition){
        BuildIEvent buildEvent = eventService.createBuildEvent(villageId, fieldPosition);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{villageId}/update-name")
    public ResponseEntity<String> updateName(@PathVariable String villageId, @RequestParam String name){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.villageService.updateName(villageId, name));
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<String> deleteEventById(@PathVariable String eventId){
        this.eventService.deleteByEventId(eventId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/armies")
    public ArmyOrderEntity orderArmyUnits(@RequestBody ArmyOrderRequest armyOrderRequest) {
        return armiesService.orderUnits(armyOrderRequest);
    }

}
