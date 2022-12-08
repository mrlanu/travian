package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.ECombatGroupLocation;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.CombatGroupSendingRequest;
import io.lanu.travian.game.models.responses.*;
import io.lanu.travian.game.services.MilitaryService;
import io.lanu.travian.game.services.SettlementService;
import io.lanu.travian.game.services.SettlementState;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/villages")
public class MilitaryController {

    private final SettlementState state;

    private final MilitaryService militaryService;
    private final SettlementService settlementService;

    public MilitaryController(SettlementState state, MilitaryService militaryService, SettlementService settlementService) {
        this.state = state;
        this.militaryService = militaryService;
        this.settlementService = settlementService;
    }

    @GetMapping("/{villageId}/combat-group")
    public Map<ECombatGroupLocation, List<CombatGroupView>> getAllCombatGroupsByVillageId(@PathVariable String villageId){
        var settlementState = state.recalculateCurrentState(villageId);
        return militaryService.getAllCombatGroupsByVillage(settlementState);
    }

    //can't be called alone(getVillageBtId should be called first for recalculation)
    @GetMapping("/{villageId}/troop-movements")
    public Map<String, TroopMovementsBrief> getTroopMovements(@PathVariable String villageId){
        return militaryService.getTroopMovementsBrief(villageId);
    }

    @PostMapping("/military")
    public VillageView orderCombatUnits(@RequestBody OrderCombatUnitRequest orderCombatUnitRequest) {
        var settlementState = state.recalculateCurrentState(orderCombatUnitRequest.getVillageId());
        settlementState = militaryService.orderCombatUnits(orderCombatUnitRequest, settlementState);
        state.save(settlementState);
        return settlementService.getVillageById(settlementState);
    }

    @GetMapping("/{villageId}/military/researched")
    public List<CombatUnitResponse> getAllResearchedUnits(@PathVariable String villageId){
        state.recalculateCurrentState(villageId);
        return militaryService.getAllResearchedUnits(villageId);
    }

    @PostMapping("/{settlementId}/check-troops-send")
    public CombatGroupContractResponse checkTroopsSendingRequest(@PathVariable String settlementId,
                                                                 @RequestBody CombatGroupSendingRequest combatGroupSendingRequest) {
        var settlementState = state.recalculateCurrentState(settlementId);
        var targetState = state.recalculateCurrentState(combatGroupSendingRequest.getTargetSettlementId());
       return militaryService.checkTroopsSendingRequest(settlementState, targetState, combatGroupSendingRequest);
    }

    @PostMapping("/troops-send")
    public boolean sendTroops(@RequestBody CombatGroupContractResponse combatGroupContractResponse){
        var settlementState = state.recalculateCurrentState(combatGroupContractResponse.getOriginVillageId());
        settlementState = militaryService.sendTroops(combatGroupContractResponse, settlementState);
        state.save(settlementState);
        return true;
    }

}

