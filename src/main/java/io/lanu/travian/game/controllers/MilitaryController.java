package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.errors.UserErrorException;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.CombatUnitOrderResponse;
import io.lanu.travian.game.models.responses.MilitaryUnitView;
import io.lanu.travian.game.models.responses.MilitaryUnitContract;
import io.lanu.travian.game.services.IState;
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

    private final IState state;
    private final VillageService villageService;
    private final MilitaryService militaryService;

    public MilitaryController(IState state, VillageService villageService, MilitaryService militaryService) {
        this.state = state;
        this.villageService = villageService;
        this.militaryService = militaryService;
    }

    @GetMapping("/{villageId}/military-units")
    public Map<String, List<MilitaryUnitView>> getAllMilitaryUnitsByVillageId(@PathVariable String villageId){
        var currentState = state.getState(villageId);
        return militaryService.getAllMilitaryUnitsByVillage(currentState);
    }

    @PostMapping("/military")
    public void orderCombatUnits(@RequestBody OrderCombatUnitRequest orderCombatUnitRequest) {
        var currentState = state.getState(orderCombatUnitRequest.getVillageId());
        currentState = militaryService.orderCombatUnits(orderCombatUnitRequest, currentState);
        state.saveState(currentState);
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
        var attackingVillage = state.getState(troopsSendingRequest.getVillageId());
        var attackedVillageOpt = villageService
                .findVillageByCoordinates(troopsSendingRequest.getX(), troopsSendingRequest.getY());
        VillageEntity attackedVillage;
        if (attackedVillageOpt.isPresent()) {
            attackedVillage = state.getState(attackedVillageOpt.get().getVillageId());
        }else {
            throw new UserErrorException("There is nothing on those coordinates");
        }
        return militaryService.checkTroopsSendingRequest(troopsSendingRequest, attackingVillage, attackedVillage);
    }

    @PostMapping("/troops-send")
    public boolean sendTroops(@RequestBody MilitaryUnitContract militaryUnitContract){
        var currentState = state.getState(militaryUnitContract.getOriginVillageId());
        currentState = militaryService.sendTroops(militaryUnitContract, currentState);
        villageService.saveVillage(currentState);
        return true;
    }

}

