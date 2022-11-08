package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.EBuilding;
import io.lanu.travian.game.models.responses.NewBuilding;
import io.lanu.travian.game.services.ConstructionService;
import io.lanu.travian.game.services.SettlementState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villages")
public class ConstructionController {

    private final SettlementState state;
    private final ConstructionService constructionService;

    public ConstructionController(SettlementState state, ConstructionService constructionService) {
        this.state = state;
        this.constructionService = constructionService;
    }

    @GetMapping("/{villageId}/buildings")
    public List<NewBuilding> getListOfAllNewBuildings(@PathVariable String villageId){
        var settlementState = state.recalculateCurrentState(villageId);
        return constructionService.getListOfAllNewBuildings(settlementState);
    }

    @PutMapping("/{villageId}/buildings/{position}/new")
    public ResponseEntity<String> newBuilding(@PathVariable String villageId,
                                              @PathVariable Integer position,
                                              @RequestParam EBuilding kind){
        var settlementState = state.recalculateCurrentState(villageId);
        settlementState = constructionService.createBuildEvent(settlementState, position, kind);
        state.save(settlementState);
        return ResponseEntity.status(HttpStatus.CREATED).body("Done");
    }

    @PutMapping("/{villageId}/buildings/{position}/upgrade")
    public ResponseEntity<String> upgradeBuilding(@PathVariable String villageId, @PathVariable Integer position){
        var settlementState = state.recalculateCurrentState(villageId);
        settlementState = constructionService.createBuildEvent(settlementState, position, null);
        state.save(settlementState);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{villageId}/events/{eventId}")
    public ResponseEntity<String> deleteEventById(@PathVariable String eventId, @PathVariable String villageId){
        var settlementState = state.recalculateCurrentState(villageId);
        settlementState = constructionService.deleteBuildingEvent(settlementState, eventId);
        state.save(settlementState);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
