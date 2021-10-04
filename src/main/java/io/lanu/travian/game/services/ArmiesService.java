package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.ArmyOrderEntity;
import io.lanu.travian.game.entities.events.TroopBuildEvent;
import io.lanu.travian.game.models.requests.ArmyOrderRequest;

import java.util.List;

public interface ArmiesService {
    List<ArmyOrderEntity> getAllOrdersByVillageId(String villageId);
    ArmyOrderEntity orderUnits(ArmyOrderRequest armyOrderRequest);
    List<TroopBuildEvent> createTroopsBuildEventsFromOrders(String villageId);
}
