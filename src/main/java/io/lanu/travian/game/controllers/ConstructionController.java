package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.EBuilding;
import io.lanu.travian.game.models.responses.NewBuilding;
import io.lanu.travian.game.services.IState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villages")
public class ConstructionController {

    private final IState state;

    public ConstructionController(IState state) {
        this.state = state;
    }

    @GetMapping("/{villageId}/buildings")
    public List<NewBuilding> getListOfAllNewBuildings(@PathVariable String villageId){
        return state.getListOfAllNewBuildings(villageId);
    }

    @PutMapping("/{villageId}/buildings/{position}/new")
    public ResponseEntity<String> newBuilding(@PathVariable String villageId,
                                              @PathVariable Integer position,
                                              @RequestParam EBuilding kind){
        state.createBuildEvent(villageId, position, kind);
        return ResponseEntity.status(HttpStatus.CREATED).body("Done");
    }

    @PutMapping("/{villageId}/buildings/{position}/upgrade")
    public ResponseEntity<String> upgradeBuilding(@PathVariable String villageId, @PathVariable Integer position){
       state.createBuildEvent(villageId, position, null);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{villageId}/events/{eventId}")
    public ResponseEntity<String> deleteEventById(@PathVariable String eventId, @PathVariable String villageId){
        state.deleteBuildingEvent(villageId, eventId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
