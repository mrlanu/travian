package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.CombatUnitDoneEvent;
import io.lanu.travian.game.entities.events.MilitaryEvent;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.MilitaryUnit;
import io.lanu.travian.game.models.responses.MilitaryUnitContract;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MilitaryService {
    List<CombatUnitDoneEvent> createCombatUnitDoneEventsFromOrders(String villageId);
    List<OrderCombatUnitEntity> getAllOrdersByVillageId(String villageId);
    VillageEntity orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest, VillageEntity village);
    List<ECombatUnit> getAllResearchedUnits(String villageId);
    Map<String, List<MilitaryUnit>> getAllMilitaryUnitsByVillage(VillageEntity village);
    MilitaryUnitContract checkTroopsSendingRequest(TroopsSendingRequest troopsSendingRequest, VillageEntity village, VillageEntity attackedVillage);
    VillageEntity sendTroops(MilitaryUnitContract militaryUnitContract, VillageEntity village);
}
