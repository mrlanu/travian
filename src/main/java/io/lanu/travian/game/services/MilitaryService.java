package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.game.entities.events.MilitaryEvent;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.MilitaryUnit;
import io.lanu.travian.game.models.responses.MilitaryUnitContract;

import java.util.List;
import java.util.Map;

public interface MilitaryService {
    List<OrderCombatUnitEntity> getAllOrdersByVillageId(String villageId);
    OrderCombatUnitEntity orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest);
    List<ECombatUnit> getAllResearchedUnits(String villageId);
    Map<String, List<MilitaryUnit>> getAllMilitaryUnitsByVillageId(String villageId);
    MilitaryUnitContract checkTroopsSendingRequest(TroopsSendingRequest troopsSendingRequest);
    boolean sendTroops(MilitaryUnitContract militaryUnitContract);
}
