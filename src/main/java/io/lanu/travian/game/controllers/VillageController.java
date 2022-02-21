package io.lanu.travian.game.controllers;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.VillageView;
import io.lanu.travian.game.services.SettlementState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/villages")
public class VillageController {

    private final SettlementState state;

    public VillageController(SettlementState state) {
        this.state = state;
    }

    @PostMapping("/create-new-village")
    public ResponseEntity<String> newVillage(@RequestBody NewVillageRequest newVillageRequest){
        SettlementEntity settlementEntity = state.newVillage(newVillageRequest);
        return ResponseEntity.status(HttpStatus.OK).body("New Village ID : " + settlementEntity.getId());
    }

    @GetMapping("/{villageId}")
    public VillageView getVillageById(@PathVariable String villageId){
        return state.getVillageById(villageId);
    }

    @PutMapping("/{villageId}/update-name")
    public ResponseEntity<String> updateVillageName(@PathVariable String villageId, @RequestParam String name){
        state.updateVillageName(villageId, name);
        return ResponseEntity.status(HttpStatus.CREATED).body("All set");
    }
}
