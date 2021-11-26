package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;

import java.util.List;

public interface MilitaryService {
    List<OrderCombatUnitEntity> getAllOrdersByVillageId(String villageId);
    OrderCombatUnitEntity orderUnits(OrderCombatUnitRequest orderCombatUnitRequest);
    List<ECombatUnit> getAllResearchedUnits(String villageId);
}
