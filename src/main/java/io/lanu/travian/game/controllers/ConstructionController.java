package io.lanu.travian.game.controllers;


import io.lanu.travian.game.models.buildings.BuildingsID;
import io.lanu.travian.game.models.responses.NewBuilding;
import io.lanu.travian.game.models.responses.SettlementView;
import io.lanu.travian.game.services.ConstructionService;
import io.lanu.travian.game.services.SettlementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villages")
public class ConstructionController {

    private final ConstructionService constructionService;
    private final SettlementService settlementService;

    public ConstructionController(ConstructionService constructionService, SettlementService settlementService) {
        this.constructionService = constructionService;
        this.settlementService = settlementService;
    }

    @PutMapping("/{settlementId}/buildings/{position}/new")
    public SettlementView newBuilding(@PathVariable String settlementId,
                                      @PathVariable Integer position,
                                      @RequestParam BuildingsID buildingID){
        var currentState = constructionService.createBuildEvent(settlementId, position, buildingID);
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
}
