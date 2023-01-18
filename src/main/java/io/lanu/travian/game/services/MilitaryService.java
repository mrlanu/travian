package io.lanu.travian.game.services;

import io.lanu.travian.enums.ENation;
import io.lanu.travian.game.dto.SettlementStateDTO;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.requests.CombatGroupSendingRequest;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.responses.CombatGroupContractResponse;
import io.lanu.travian.game.models.responses.CombatUnitResponse;

import java.util.List;

public interface MilitaryService {
    SettlementStateDTO orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest, String settlementId);
    List<CombatUnitResponse> getAllResearchedUnits(String villageId, ENation nations);
    SettlementStateDTO sendTroops(SettlementStateDTO settlementState, String contractId);
    CombatGroupContractResponse checkTroopsSendingRequest(SettlementEntity settlementState,
                                                          SettlementEntity targetState,
                                                          CombatGroupSendingRequest combatGroupSendingRequest);
}
