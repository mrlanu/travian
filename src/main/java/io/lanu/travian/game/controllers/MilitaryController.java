package io.lanu.travian.game.controllers;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.CombatGroupSendingRequest;
import io.lanu.travian.game.models.responses.CombatGroupSendingContract;
import io.lanu.travian.game.models.responses.CombatGroupView;
import io.lanu.travian.game.models.responses.TroopMovementsBrief;
import io.lanu.travian.game.models.responses.VillageView;
import io.lanu.travian.game.services.MilitaryService;
import io.lanu.travian.game.services.SettlementRepository;
import io.lanu.travian.game.services.SettlementState;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/villages")
public class MilitaryController {

    private final SettlementState state;

    private final MilitaryService militaryService;
    private final SettlementRepository settlementRepository;

    public MilitaryController(SettlementState state, MilitaryService militaryService, SettlementRepository settlementRepository) {
        this.state = state;
        this.militaryService = militaryService;
        this.settlementRepository = settlementRepository;
    }

    @GetMapping("/{villageId}/combat-group")
    public Map<String, List<CombatGroupView>> getAllCombatGroupsByVillageId(@PathVariable String villageId){
        var settlementState = state.recalculateCurrentState(villageId);
        return militaryService.getAllMilitaryUnitsByVillage(settlementState);
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
        return settlementRepository.getVillageById(settlementState);
    }

    @GetMapping("/{villageId}/military/researched")
    public List<ECombatUnit> getAllResearchedUnits(@PathVariable String villageId){
        state.recalculateCurrentState(villageId);
        return militaryService.getAllResearchedUnits(villageId);
    }

    @PostMapping("/check-troops-send")
    public CombatGroupSendingContract checkTroopsSendingRequest(@RequestBody CombatGroupSendingRequest combatGroupSendingRequest) {
        var settlementState = state.recalculateCurrentState(combatGroupSendingRequest.getVillageId());
       return militaryService.checkTroopsSendingRequest(settlementState, combatGroupSendingRequest);
    }

    @PostMapping("/troops-send")
    public boolean sendTroops(@RequestBody CombatGroupSendingContract combatGroupSendingContract){
        var settlementState = state.recalculateCurrentState(combatGroupSendingContract.getOriginVillageId());
        settlementState = militaryService.sendTroops(combatGroupSendingContract, settlementState);
        state.save(settlementState);
        return true;
    }

}

