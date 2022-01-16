package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.CombatUnitOrderResponse;
import io.lanu.travian.game.models.responses.MilitaryUnit;
import io.lanu.travian.game.models.responses.MilitaryUnitContract;
import io.lanu.travian.game.services.MilitaryService;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/villages")
public class MilitaryController {

    private final MilitaryService militaryService;

    public MilitaryController(MilitaryService militaryService) {
        this.militaryService = militaryService;
    }

    @GetMapping("/{villageId}/military-units")
    public Map<String, List<MilitaryUnit>> getAllMilitaryUnitsByVillageId(@PathVariable String villageId){

        return militaryService.getAllMilitaryUnitsByVillageId(villageId);
    }

    @PostMapping("/military")
    public CombatUnitOrderResponse orderArmyUnits(@RequestBody OrderCombatUnitRequest orderCombatUnitRequest) {
        var result = militaryService.orderCombatUnits(orderCombatUnitRequest);
        return new CombatUnitOrderResponse(result.getUnitType().getName(), result.getLeftTrain(),
                result.getDurationEach(), result.getDurationEach() * result.getLeftTrain(), result.getEndOrderTime());
    }

    @GetMapping("/{villageId}/military-orders")
    public List<CombatUnitOrderResponse> getAllMilitaryOrders(@PathVariable String villageId){
        return militaryService.getAllOrdersByVillageId(villageId)
                .stream()
                .map(armyOrderEntity -> {
                    var duration = Duration.between(LocalDateTime.now(), armyOrderEntity.getEndOrderTime()).toSeconds();
                    return new CombatUnitOrderResponse(
                        armyOrderEntity.getUnitType().getName(),
                        armyOrderEntity.getLeftTrain(),
                        duration,
                        armyOrderEntity.getDurationEach(),
                        armyOrderEntity.getEndOrderTime());})
                .collect(Collectors.toList());
    }

    @GetMapping("/{villageId}/military/researched")
    public List<ECombatUnit> getAllResearchedUnits(@PathVariable String villageId){
        return this.militaryService.getAllResearchedUnits(villageId);
    }

    @PostMapping("/check-troops-send")
    public MilitaryUnitContract checkTroopsSendingRequest(@RequestBody TroopsSendingRequest troopsSendingRequest) {
        return militaryService.checkTroopsSendingRequest(troopsSendingRequest);
    }

    @PostMapping("/troops-send")
    public boolean sendTroops(@RequestBody MilitaryUnitContract militaryUnitContract){
        return militaryService.sendTroops(militaryUnitContract);
    }

}

