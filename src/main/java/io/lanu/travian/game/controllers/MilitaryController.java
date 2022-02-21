package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.CombatUnitOrderResponse;
import io.lanu.travian.game.models.responses.MilitaryUnitContract;
import io.lanu.travian.game.models.responses.MilitaryUnitView;
import io.lanu.travian.game.services.SettlementState;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/villages")
public class MilitaryController {

    private final SettlementState state;

    public MilitaryController(SettlementState state) {
        this.state = state;
    }

    @GetMapping("/{villageId}/military-units")
    public Map<String, List<MilitaryUnitView>> getAllMilitaryUnitsByVillageId(@PathVariable String villageId){
        return state.getAllMilitaryUnitsByVillage(villageId);
    }

    @PostMapping("/military")
    public void orderCombatUnits(@RequestBody OrderCombatUnitRequest orderCombatUnitRequest) {
        state.orderCombatUnits(orderCombatUnitRequest);
    }

    @GetMapping("/{villageId}/military-orders")
    public List<CombatUnitOrderResponse> getAllMilitaryOrders(@PathVariable String villageId){
        return state.getAllOrdersByVillageId(villageId);
    }

    @GetMapping("/{villageId}/military/researched")
    public List<ECombatUnit> getAllResearchedUnits(@PathVariable String villageId){
        return state.getAllResearchedUnits(villageId);
    }

    @PostMapping("/check-troops-send")
    public MilitaryUnitContract checkTroopsSendingRequest(@RequestBody TroopsSendingRequest troopsSendingRequest) {
        return state.checkTroopsSendingRequest(troopsSendingRequest);
    }

    @PostMapping("/troops-send")
    public boolean sendTroops(@RequestBody MilitaryUnitContract militaryUnitContract){
        state.sendTroops(militaryUnitContract);
        return true;
    }

}

