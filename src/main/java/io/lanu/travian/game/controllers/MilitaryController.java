package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.EUnits;
import io.lanu.travian.game.models.requests.ArmyOrderRequest;
import io.lanu.travian.game.models.responses.ArmyOrderResponse;
import io.lanu.travian.game.services.MilitaryService;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/villages")
public class MilitaryController {

    private final MilitaryService militaryService;

    public MilitaryController(MilitaryService militaryService) {
        this.militaryService = militaryService;
    }

    @PostMapping("/military")
    public ArmyOrderResponse orderArmyUnits(@RequestBody ArmyOrderRequest armyOrderRequest) {
        var result = militaryService.orderUnits(armyOrderRequest);
        return new ArmyOrderResponse(result.getUnitType().getName(), result.getLeftTrain(),
                result.getDurationEach(), result.getDurationEach() * result.getLeftTrain(), result.getEndOrderTime());
    }

    @GetMapping("/{villageId}/military-orders")
    public List<ArmyOrderResponse> getAllMilitaryOrders(@PathVariable String villageId){
        return militaryService.getAllOrdersByVillageId(villageId)
                .stream()
                .map(armyOrderEntity -> {
                    var duration = Duration.between(LocalDateTime.now(), armyOrderEntity.getEndOrderTime()).toSeconds();
                    return new ArmyOrderResponse(
                        armyOrderEntity.getUnitType().getName(),
                        armyOrderEntity.getLeftTrain(),
                        duration,
                        armyOrderEntity.getDurationEach(),
                        armyOrderEntity.getEndOrderTime());})
                .collect(Collectors.toList());
    }

    @GetMapping("/{villageId}/military/researched")
    public List<EUnits> getAllResearchedUnits(@PathVariable String villageId){
        return this.militaryService.getAllResearchedUnits(villageId);
    }
}
