package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatGroupLocation;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.requests.CombatGroupSendingRequest;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.responses.CombatGroupContractResponse;
import io.lanu.travian.game.models.responses.CombatGroupView;
import io.lanu.travian.game.models.responses.CombatUnitResponse;
import io.lanu.travian.game.models.responses.TroopMovementsBrief;

import java.util.List;
import java.util.Map;

public interface MilitaryService {
    SettlementEntity orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest, String settlementId);
    List<CombatUnitResponse> getAllResearchedUnits(String villageId);
    Map<ECombatGroupLocation, List<CombatGroupView>> getAllCombatGroupsByVillage(SettlementEntity village);
    SettlementEntity sendTroops(SettlementEntity settlementState, String contractId);
    Map<String, TroopMovementsBrief> getTroopMovementsBrief(String settlementId);
    CombatGroupContractResponse checkTroopsSendingRequest(SettlementEntity settlementState,
                                                          SettlementEntity targetState,
                                                          CombatGroupSendingRequest combatGroupSendingRequest);
}
