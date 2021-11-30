package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.MilitaryUnitResponse;
import io.lanu.travian.game.models.responses.TroopsSendingResponse;

import java.util.List;

public interface MilitaryService {
    List<OrderCombatUnitEntity> getAllOrdersByVillageId(String villageId);
    OrderCombatUnitEntity orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest);
    List<ECombatUnit> getAllResearchedUnits(String villageId);
    List<MilitaryUnitResponse> getAllMilitaryUnitsByVillageId(String villageId);
    TroopsSendingResponse checkTroopsSendingRequest(TroopsSendingRequest troopsSendingRequest);
}
