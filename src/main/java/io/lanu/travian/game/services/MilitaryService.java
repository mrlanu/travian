package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.MilitaryUnitEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.models.events.EventStrategy;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.MilitaryUnitContract;
import io.lanu.travian.game.models.responses.MilitaryUnitView;

import java.util.List;
import java.util.Map;

public interface MilitaryService {
    List<EventStrategy> createCombatUnitDoneEventsFromOrders(VillageEntity origin);
    List<OrderCombatUnitEntity> getAllOrdersByVillageId(String villageId);
    VillageEntity orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest, VillageEntity village);
    List<ECombatUnit> getAllResearchedUnits(String villageId);
    Map<String, List<MilitaryUnitView>> getAllMilitaryUnitsByVillage(VillageEntity village);
    List<MovedMilitaryUnitEntity> getAllMovedUnitsByOriginVillageId(String villageId);
    MilitaryUnitContract checkTroopsSendingRequest(TroopsSendingRequest troopsSendingRequest, VillageEntity village, VillageEntity attackedVillage);
    VillageEntity sendTroops(MilitaryUnitContract militaryUnitContract, VillageEntity village);
    MilitaryUnitEntity saveMilitaryUnit(MilitaryUnitEntity unit);
    MovedMilitaryUnitEntity saveMovedMilitaryUnit(MovedMilitaryUnitEntity unit);
    void deleteMovedUnitById(String id);
    void deleteUnitById(String id);
}
