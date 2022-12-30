package io.lanu.travian.game.services;

import io.lanu.travian.enums.EBuilding;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.ConstructionEventEntity;
import io.lanu.travian.game.models.responses.NewBuilding;

import java.util.List;

public interface ConstructionService {

    SettlementEntity createBuildEvent(String settlementId, Integer fieldPosition, EBuilding kind);

    SettlementEntity deleteBuildingEvent(String settlementId, String eventId);

    List<NewBuilding> getListOfAllNewBuildings(String settlementId);
}
