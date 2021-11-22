package io.lanu.travian.game.services;

import io.lanu.travian.enums.EUnits;
import io.lanu.travian.game.entities.ArmyOrderEntity;
import io.lanu.travian.game.models.requests.ArmyOrderRequest;

import java.util.List;

public interface MilitaryService {
    List<ArmyOrderEntity> getAllOrdersByVillageId(String villageId);
    ArmyOrderEntity orderUnits(ArmyOrderRequest armyOrderRequest);
    List<EUnits> getAllResearchedUnits(String villageId);
}
