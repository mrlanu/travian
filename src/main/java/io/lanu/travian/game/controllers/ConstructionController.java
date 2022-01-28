package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.EBuilding;
import io.lanu.travian.game.models.responses.NewBuilding;
import io.lanu.travian.game.services.IConstructionService;
import io.lanu.travian.game.services.IState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villages")
public class ConstructionController {
    private final IConstructionService constructionService;
    private final IState state;

    public ConstructionController(IConstructionService constructionService, IState state) {
        this.constructionService = constructionService;
        this.state = state;
    }

    @GetMapping("/{villageId}/buildings")
    public List<NewBuilding> getListOfAllNewBuildings(@PathVariable String villageId){
        var currentState = state.getState(villageId);
        return constructionService.getListOfAllNewBuildings(currentState);
    }

    @PutMapping("/{villageId}/buildings/{position}/new")
    public ResponseEntity<String> newBuilding(@PathVariable String villageId,
                                              @PathVariable Integer position,
                                              @RequestParam EBuilding kind){
        var currentState = state.getState(villageId);
        currentState = constructionService.createBuildEvent(currentState, position, kind);
        state.saveState(currentState);
        return ResponseEntity.status(HttpStatus.CREATED).body("Done");
    }

    @PutMapping("/{villageId}/buildings/{position}/upgrade")
    public ResponseEntity<String> upgradeBuilding(@PathVariable String villageId, @PathVariable Integer position){
        var currentState = state.getState(villageId);
        currentState = constructionService.createBuildEvent(currentState, position, null);
        state.saveState(currentState);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{villageId}/events/{eventId}")
    public ResponseEntity<String> deleteEventById(@PathVariable String villageId, @PathVariable String eventId){
        var currentState = state.getState(villageId);
        currentState = constructionService.deleteBuildingEvent(currentState, eventId);
        state.saveState(currentState);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
