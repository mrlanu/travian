package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.CombatUnitOrderResponse;
import io.lanu.travian.game.models.responses.MilitaryUnitContract;
import io.lanu.travian.game.models.responses.CombatGroupView;
import io.lanu.travian.game.models.responses.TroopMovementsBrief;
import io.lanu.travian.game.services.MilitaryService;
import io.lanu.travian.game.services.SettlementState;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/villages")
public class MilitaryController {

    private final SettlementState state;

    private final MilitaryService militaryService;

    public MilitaryController(SettlementState state, MilitaryService militaryService) {
        this.state = state;
        this.militaryService = militaryService;
    }

    @GetMapping("/{villageId}/military-units")
    public Map<String, List<CombatGroupView>> getAllMilitaryUnitsByVillageId(@PathVariable String villageId){
        var settlementState = state.recalculateCurrentState(villageId);
        return militaryService.getAllMilitaryUnitsByVillage(settlementState);
    }

    @GetMapping("/{villageId}/troop-movements")
    public Map<String, TroopMovementsBrief> getTroopMovements(@PathVariable String villageId){
        var settlementState = state.recalculateCurrentState(villageId);
        return militaryService.getTroopMovementsBrief(settlementState.getId());
    }

    @PostMapping("/military")
    public void orderCombatUnits(@RequestBody OrderCombatUnitRequest orderCombatUnitRequest) {
        var settlementState = state.recalculateCurrentState(orderCombatUnitRequest.getVillageId());
        settlementState = militaryService.orderCombatUnits(orderCombatUnitRequest, settlementState);
        state.save(settlementState);
    }

    @GetMapping("/{villageId}/military-orders")
    public List<CombatUnitOrderResponse> getAllMilitaryOrders(@PathVariable String villageId){
        state.recalculateCurrentState(villageId);
        return militaryService.getAllOrdersByVillageId(villageId);
    }

    @GetMapping("/{villageId}/military/researched")
    public List<ECombatUnit> getAllResearchedUnits(@PathVariable String villageId){
        state.recalculateCurrentState(villageId);
        return militaryService.getAllResearchedUnits(villageId);
    }

    @PostMapping("/check-troops-send")
    public MilitaryUnitContract checkTroopsSendingRequest(@RequestBody TroopsSendingRequest troopsSendingRequest) {
        var settlementState = state.recalculateCurrentState(troopsSendingRequest.getVillageId());
       return militaryService.checkTroopsSendingRequest(settlementState, troopsSendingRequest);
    }

    @PostMapping("/troops-send")
    public boolean sendTroops(@RequestBody MilitaryUnitContract militaryUnitContract){
        var settlementState = state.recalculateCurrentState(militaryUnitContract.getOriginVillageId());
        settlementState = militaryService.sendTroops(militaryUnitContract, settlementState);
        state.save(settlementState);
        return true;
    }

}

