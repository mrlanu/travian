package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.EUnits;
import io.lanu.travian.game.entities.ArmyOrderEntity;
import io.lanu.travian.game.models.requests.ArmyOrderRequest;
import io.lanu.travian.game.services.MilitaryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villages")
public class MilitaryController {

    private final MilitaryService militaryService;

    public MilitaryController(MilitaryService militaryService) {
        this.militaryService = militaryService;
    }

    @PostMapping("/armies")
    public ArmyOrderEntity orderArmyUnits(@RequestBody ArmyOrderRequest armyOrderRequest) {
        return militaryService.orderUnits(armyOrderRequest);
    }

    @GetMapping("/{villageId}/military/researched")
    public List<EUnits> getAllResearchedUnits(@PathVariable String villageId){
        return this.militaryService.getAllResearchedUnits(villageId);
    }
}
