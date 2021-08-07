package io.lanu.travian.game.controllers;

import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.services.VillageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/villages")
public class VillageController {
    private final VillageService villageService;

    public VillageController(VillageService villageService) {
        this.villageService = villageService;
    }

    @GetMapping("/{villageId}")
    public VillageEntity getVillageById(@PathVariable String villageId){
        return villageService.getVillageById(villageId);
    }


    @PostMapping("/create-new-village")
    public ResponseEntity<String> newVillage(@RequestBody NewVillageRequest newVillageRequest){
        VillageEntity villageEntity = villageService.createVillage(newVillageRequest);
        return ResponseEntity.status(HttpStatus.OK).body("New Village ID : " + villageEntity.getVillageId());
    }
}
