package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.MilitaryUnitEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.CombatUnitOrderResponse;
import io.lanu.travian.game.models.responses.MilitaryUnitContract;
import io.lanu.travian.game.models.responses.MilitaryUnitView;
import io.lanu.travian.game.models.responses.TroopMovementsResponse;

import java.util.List;
import java.util.Map;

public interface MilitaryService {
    List<CombatUnitOrderResponse> getAllOrdersByVillageId(String villageId);
    SettlementEntity orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest, SettlementEntity village);
    List<ECombatUnit> getAllResearchedUnits(String villageId);
    Map<String, List<MilitaryUnitView>> getAllMilitaryUnitsByVillage(SettlementEntity village);
    //List<MovedMilitaryUnitEntity> getAllMovedUnitsByOriginVillageId(String villageId);
    SettlementEntity sendTroops(MilitaryUnitContract militaryUnitContract, SettlementEntity village);
    //void deleteUnitById(String id);
    //List<MilitaryUnitEntity> getAllByTargetVillageId(String villageId);
    //List<MovedMilitaryUnitEntity> getAllByOriginVillageIdOrTargetVillageId(String originId);
    List<TroopMovementsResponse> getTroopMovements(SettlementEntity recalculateCurrentState);
    MilitaryUnitContract checkTroopsSendingRequest(SettlementEntity settlementEntity, TroopsSendingRequest troopsSendingRequest);
}
