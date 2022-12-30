package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.EBuilding;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.dto.SettlementStateDTO;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.responses.NewBuilding;
import io.lanu.travian.game.models.responses.SettlementView;
import io.lanu.travian.game.repositories.SettlementRepository;
import io.lanu.travian.game.services.ConstructionService;
import io.lanu.travian.game.services.MilitaryService;
import io.lanu.travian.game.services.SettlementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/villages")
public class VillageController {

    private final SettlementService settlementService;
    private final ConstructionService constructionService;
    private final MilitaryService militaryService;
    private final SettlementRepository settlementRepository;

    public VillageController(SettlementService settlementService,
                             ConstructionService constructionService,
                             MilitaryService militaryService, SettlementRepository settlementRepository) {
        this.settlementService = settlementService;
        this.constructionService = constructionService;
        this.militaryService = militaryService;
        this.settlementRepository = settlementRepository;
    }

    @PostMapping("/create-new-village")
    public ResponseEntity<String> newVillage(@RequestBody NewVillageRequest newVillageRequest){
        SettlementStateDTO settlementState = settlementService.newVillage(newVillageRequest);
        return ResponseEntity.status(HttpStatus.OK).body("New Village ID : " + settlementState.getSettlementEntity().getId());
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

    //just for testing purpose, should be deleted later
    @PostMapping("/{settlementId}/add-resources")
    public void cheat(@PathVariable String settlementId) {
        var s = settlementRepository.findById(settlementId).orElseThrow();
        var storage = s.getStorage();
        storage.put(EResource.CROP, BigDecimal.valueOf(700));
        storage.put(EResource.CLAY, BigDecimal.valueOf(700));
        storage.put(EResource.WOOD, BigDecimal.valueOf(700));
        storage.put(EResource.IRON, BigDecimal.valueOf(700));
        settlementRepository.save(s);
    }

}
