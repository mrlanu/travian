package io.lanu.travian.game.controllers;

import io.lanu.travian.game.models.requests.CombatGroupSendingRequest;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.responses.CombatGroupContractResponse;
import io.lanu.travian.game.models.responses.CombatUnitResponse;
import io.lanu.travian.game.models.responses.SettlementView;
import io.lanu.travian.game.services.MilitaryService;
import io.lanu.travian.game.services.EngineService;
import io.lanu.travian.game.services.SettlementService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/villages")
public class MilitaryController {

    private final EngineService state;
    private final SettlementService settlementService;

    private final MilitaryService militaryService;

    public MilitaryController(EngineService state, SettlementService settlementService, MilitaryService militaryService) {
        this.state = state;
        this.settlementService = settlementService;
        this.militaryService = militaryService;
    }

    @GetMapping("/{villageId}/military/researched")
    public List<CombatUnitResponse> getAllResearchedUnits(@PathVariable String villageId){
        state.recalculateCurrentState(villageId, LocalDateTime.now());
        return militaryService.getAllResearchedUnits(villageId);
    }

    @PostMapping("/{settlementId}/check-troops-send")
    public CombatGroupContractResponse checkTroopsSendingRequest(@PathVariable String settlementId,
                                                                 @RequestBody CombatGroupSendingRequest combatGroupSendingRequest) {
        var settlementState = state.recalculateCurrentState(settlementId, LocalDateTime.now());
        var targetState = settlementService
                .findById(combatGroupSendingRequest.getTargetSettlementId()).orElseThrow();
       return militaryService.checkTroopsSendingRequest(settlementState.getSettlementEntity(), targetState, combatGroupSendingRequest);
    }

    @PostMapping("/{settlementId}/troops-send/{contractId}")
    public boolean sendTroops(@PathVariable String settlementId, @PathVariable String contractId){
        var settlementState = state.recalculateCurrentState(settlementId, LocalDateTime.now());
        settlementState = militaryService.sendTroops(settlementState, contractId);
        state.saveSettlementEntity(settlementState);
        return true;
    }

    @PostMapping("/{settlementId}/military")
    public SettlementView orderCombatUnits(@PathVariable String settlementId,
                                           @RequestBody OrderCombatUnitRequest orderCombatUnitRequest) {
        var currentState = militaryService.orderCombatUnits(orderCombatUnitRequest, settlementId);
        return settlementService.getSettlementById(currentState);
    }

}

