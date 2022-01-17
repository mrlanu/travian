package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.errors.UserErrorException;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.CombatUnitOrderResponse;
import io.lanu.travian.game.models.responses.MilitaryUnit;
import io.lanu.travian.game.models.responses.MilitaryUnitContract;
import io.lanu.travian.game.services.MilitaryService;
import io.lanu.travian.game.services.VillageService;
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
    private final VillageService villageService;

    public MilitaryController(MilitaryService militaryService, VillageService villageService) {
        this.militaryService = militaryService;
        this.villageService = villageService;
    }

    @GetMapping("/{villageId}/military-units")
    public Map<String, List<MilitaryUnit>> getAllMilitaryUnitsByVillageId(@PathVariable String villageId){
        var village = villageService.recalculateVillage(villageId);
        return militaryService.getAllMilitaryUnitsByVillage(village);
    }

    @PostMapping("/military")
    public void orderCombatUnits(@RequestBody OrderCombatUnitRequest orderCombatUnitRequest) {
        var village = villageService.recalculateVillage(orderCombatUnitRequest.getVillageId());
        village = militaryService.orderCombatUnits(orderCombatUnitRequest, village);
        villageService.saveVillage(village);
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
        return militaryService.getAllResearchedUnits(villageId);
    }

    @PostMapping("/check-troops-send")
    public MilitaryUnitContract checkTroopsSendingRequest(@RequestBody TroopsSendingRequest troopsSendingRequest) {
        var village = villageService.recalculateVillage(troopsSendingRequest.getVillageId());
        var attackedVillageOpt = villageService
                .findVillageByCoordinates(troopsSendingRequest.getX(), troopsSendingRequest.getY());
        VillageEntity attackedVillage;
        if (attackedVillageOpt.isPresent()) {
            attackedVillage = villageService.recalculateVillage(attackedVillageOpt.get().getVillageId());
        }else {
            throw new UserErrorException("There is nothing on those coordinates");
        }
        return militaryService.checkTroopsSendingRequest(troopsSendingRequest, village, attackedVillage);
    }

    @PostMapping("/troops-send")
    public boolean sendTroops(@RequestBody MilitaryUnitContract militaryUnitContract){
        var village = villageService.recalculateVillage(militaryUnitContract.getOriginVillageId());
        village = militaryService.sendTroops(militaryUnitContract, village);
        villageService.saveVillage(village);
        return true;
    }

}

