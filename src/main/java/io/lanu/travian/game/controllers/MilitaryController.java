package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.ECombatGroupMission;
import io.lanu.travian.game.models.battle.*;
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
        var currentState = state.updateParticularSettlementState(villageId, LocalDateTime.now());
        return militaryService.getAllResearchedUnits(villageId, currentState.getSettlementEntity().getNation());
    }

    @PostMapping("/{settlementId}/check-troops-send")
    public CombatGroupContractResponse checkTroopsSendingRequest(@PathVariable String settlementId,
                                                                 @RequestBody CombatGroupSendingRequest combatGroupSendingRequest) {
        var settlementState = state.updateParticularSettlementState(settlementId, LocalDateTime.now());
        var targetState = settlementService
                .findById(combatGroupSendingRequest.getTargetSettlementId()).orElseThrow();
       return militaryService.checkTroopsSendingRequest(settlementState.getSettlementEntity(), targetState, combatGroupSendingRequest);
    }

    @PostMapping("/{settlementId}/troops-send/{contractId}")
    public boolean sendTroops(@PathVariable String settlementId, @PathVariable String contractId){
        var settlementState = state.updateParticularSettlementState(settlementId, LocalDateTime.now());
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

    @GetMapping("/battle")
    public void battle(){
        var battle = new Battle();

        var battleField = BattleField.builder()
                .tribe(0)
                .population(100)
                .wall(new Wall(0, 1))
                .build();

        var off = Army.builder()
                .side(Army.ESide.OFF)
                .population(100)
                .units(UnitsConst.UNITS.get(2))
                .numbers(List.of(11,0,0,0,0,0,0,0,0,0))
                .mission(ECombatGroupMission.ATTACK)
                .build();

        var def = Army.builder()
                .side(Army.ESide.DEF)
                .population(100)
                .units(UnitsConst.UNITS.get(0))
                .numbers(List.of(5,0,0,0,0,0,0,0,0,0))
                .build();

        var offScan = Army.builder()
                .side(Army.ESide.OFF)
                .population(100)
                .units(UnitsConst.UNITS.get(0))
                .numbers(List.of(0,0,0,100,0,0,0,0,0,0))
                .build();

        var defScan = Army.builder()
                .side(Army.ESide.DEF)
                .population(100)
                .units(UnitsConst.UNITS.get(2))
                .numbers(List.of(0,0,100,0,0,0,0,0,0,0))
                .build();

        battle.perform(battleField, List.of(def, off));
        System.out.println("Finale grande");
    }

}

