package io.lanu.travian.game.controllers;

import io.lanu.travian.game.dto.SettlementStateDTO;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.SettlementView;
import io.lanu.travian.game.repositories.SettlementRepository;
import io.lanu.travian.game.services.SettlementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/villages")
public class VillageController {

    private final SettlementService settlementService;
    private final SettlementRepository settlementRepository;

    public VillageController(SettlementService settlementService,
                             SettlementRepository settlementRepository) {
        this.settlementService = settlementService;
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

    //just for testing purpose, should be deleted later
    @PostMapping("/{settlementId}/add-resources")
    public void cheat(@PathVariable String settlementId) {
        var s = settlementRepository.findById(settlementId).orElseThrow();
        var storage = s.getStorage();
        storage.set(0, BigDecimal.valueOf(700));
        storage.set(1, BigDecimal.valueOf(700));
        storage.set(2, BigDecimal.valueOf(700));
        storage.set(3, BigDecimal.valueOf(700));
        settlementRepository.save(s);
    }

    @GetMapping("/{settlementId}/add-army/{kind}/{amount}")
    public void cheatArmy(@PathVariable String settlementId, @PathVariable int kind, @PathVariable int amount) {
        var s = settlementRepository.findById(settlementId).orElseThrow();
        var homeLegion = s.getHomeLegion();
        homeLegion.set(kind, amount);
        settlementRepository.save(s);
    }

}
