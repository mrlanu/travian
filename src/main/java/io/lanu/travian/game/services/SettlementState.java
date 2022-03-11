package io.lanu.travian.game.services;

import io.lanu.travian.enums.EBuilding;
import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.*;

import java.util.List;
import java.util.Map;

public interface SettlementState {
    SettlementEntity newVillage(NewVillageRequest newVillageRequest);
    VillageView getVillageById(String villageId);
    SettlementEntity recalculateCurrentState(String villageId);
    void updateVillageName(String villageId, String name);

    List<NewBuilding> getListOfAllNewBuildings(String villageId);
    void createBuildEvent(String villageId, Integer position, EBuilding kind);
    void deleteBuildingEvent(String villageId, String eventId);

    Map<String, List<MilitaryUnitView>> getAllMilitaryUnitsByVillage(String villageId);
    void orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest);

    List<CombatUnitOrderResponse> getAllOrdersByVillageId(String villageId);

    List<ECombatUnit> getAllResearchedUnits(String villageId);
    MilitaryUnitContract checkTroopsSendingRequest(TroopsSendingRequest troopsSendingRequest);

    void sendTroops(MilitaryUnitContract militaryUnitContract);

    TileDetail getTileDetail(String id, int fromX, int fromY);

    List<TroopMovementsResponse> getTroopMovements(String villageId);
}
