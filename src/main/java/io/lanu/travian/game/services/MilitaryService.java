package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatGroupLocation;
import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.requests.CombatGroupSendingRequest;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.responses.CombatGroupSendingContract;
import io.lanu.travian.game.models.responses.CombatGroupView;
import io.lanu.travian.game.models.responses.TroopMovementsBrief;

import java.util.List;
import java.util.Map;

public interface MilitaryService {
    SettlementEntity orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest, SettlementEntity village);
    List<ECombatUnit> getAllResearchedUnits(String villageId);
    Map<ECombatGroupLocation, List<CombatGroupView>> getAllCombatGroupsByVillage(SettlementEntity village);
    SettlementEntity sendTroops(CombatGroupSendingContract combatGroupSendingContract, SettlementEntity village);
    Map<String, TroopMovementsBrief> getTroopMovementsBrief(String settlementId);
    CombatGroupSendingContract checkTroopsSendingRequest(SettlementEntity settlementEntity, CombatGroupSendingRequest combatGroupSendingRequest);
}
