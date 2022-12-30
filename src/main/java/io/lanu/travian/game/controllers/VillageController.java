package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.EBuilding;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.responses.NewBuilding;
import io.lanu.travian.game.models.responses.SettlementView;
import io.lanu.travian.game.services.ConstructionService;
import io.lanu.travian.game.services.MilitaryService;
import io.lanu.travian.game.services.SettlementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/villages")
public class VillageController {

    private final SettlementService settlementService;
    private final ConstructionService constructionService;
    private final MilitaryService militaryService;

    public VillageController(SettlementService settlementService,
                             ConstructionService constructionService,
                             MilitaryService militaryService) {
        this.settlementService = settlementService;
        this.constructionService = constructionService;
        this.militaryService = militaryService;
    }

    @PostMapping("/create-new-village")
    public ResponseEntity<String> newVillage(@RequestBody NewVillageRequest newVillageRequest){
        SettlementEntity settlementEntity = settlementService.newVillage(newVillageRequest);
        return ResponseEntity.status(HttpStatus.OK).body("New Village ID : " + settlementEntity.getId());
    }

    @GetMapping("/{settlementId}")
    public SettlementView getSettlementById(@PathVariable String settlementId){
        return settlementService.getSettlementById(settlementId);
    }

    @PutMapping("/{villageId}/update-name")
    public SettlementView updateVillageName(@PathVariable String villageId, @RequestParam String name){
        var currentState = settlementService.updateName(villageId, name);
        return settlementService.getSettlementById(currentState);
    }

    @PutMapping("/{settlementId}/buildings/{position}/new")
    public SettlementView newBuilding(@PathVariable String settlementId,
                                              @PathVariable Integer position,
                                              @RequestParam EBuilding kind){
        var currentState = constructionService.createBuildEvent(settlementId, position, kind);
        return settlementService.getSettlementById(currentState);
    }

    @PutMapping("/{settlementId}/buildings/{position}/upgrade")
    public SettlementView upgradeBuilding(@PathVariable String settlementId, @PathVariable Integer position){
        var currentState = constructionService.createBuildEvent(settlementId, position, null);
        return settlementService.getSettlementById(currentState);
    }

    @DeleteMapping("/{settlementId}/events/{eventId}")
    public SettlementView deleteEventById(@PathVariable String eventId, @PathVariable String settlementId){
        var currentState = constructionService.deleteBuildingEvent(settlementId, eventId);
        return settlementService.getSettlementById(currentState);
    }

    @GetMapping("/{settlementId}/buildings")
    public List<NewBuilding> getListOfAllNewBuildings(@PathVariable String settlementId){
        return constructionService.getListOfAllNewBuildings(settlementId);
    }

    @PostMapping("/{settlementId}/military")
    public SettlementView orderCombatUnits(@PathVariable String settlementId,
                                           @RequestBody OrderCombatUnitRequest orderCombatUnitRequest) {
        var currentState = militaryService.orderCombatUnits(orderCombatUnitRequest, settlementId);
        return settlementService.getSettlementById(currentState);
    }

}
