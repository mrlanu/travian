package io.lanu.travian.game.services;

import io.lanu.travian.game.dto.SettlementStateDTO;
import io.lanu.travian.game.models.buildings.BuildingsID;
import io.lanu.travian.game.models.responses.NewBuilding;

import java.util.List;

public interface ConstructionService {

    SettlementStateDTO createBuildEvent(String settlementId, Integer fieldPosition, BuildingsID buildingID);

    SettlementStateDTO deleteBuildingEvent(String settlementId, String eventId);

    List<NewBuilding> getListOfAllNewBuildings(String settlementId);
}
