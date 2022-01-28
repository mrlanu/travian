package io.lanu.travian.game.controllers;

import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.VillageView;
import io.lanu.travian.game.services.IState;
import io.lanu.travian.game.services.VillageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/villages")
public class VillageController {
    private final IState state;
    private final VillageService villageService;

    public VillageController(IState state, VillageService villageService) {
        this.state = state;
        this.villageService = villageService;
    }

    @PostMapping("/create-new-village")
    public ResponseEntity<String> newVillage(@RequestBody NewVillageRequest newVillageRequest){
        VillageEntity villageEntity = villageService.createVillage(newVillageRequest);
        return ResponseEntity.status(HttpStatus.OK).body("New Village ID : " + villageEntity.getVillageId());
    }

    @GetMapping("/{villageId}")
    public VillageView getVillageById(@PathVariable String villageId){
        var villageState = state.getState(villageId);
        state.saveState(villageState);
        return villageService.getVillageById(villageState);
    }

    @PutMapping("/{villageId}/update-name")
    public ResponseEntity<String> updateName(@PathVariable String villageId, @RequestParam String name){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.villageService.updateName(villageId, name));
    }
}
