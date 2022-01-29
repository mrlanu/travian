package io.lanu.travian.game.services;

import io.lanu.travian.enums.EBuilding;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.ConstructionEventEntity;
import io.lanu.travian.game.models.responses.NewBuilding;

import java.time.LocalDateTime;
import java.util.List;

public interface IConstructionService {

    VillageEntity createBuildEvent(VillageEntity village, Integer fieldPosition, EBuilding kind);

    List<ConstructionEventEntity> findAllByVillageId(String villageId);

    VillageEntity deleteBuildingEvent(VillageEntity village, String eventId);

    void deleteAllByVillageIdAndExecutionTimeBefore(String villageId, LocalDateTime time);

    List<NewBuilding> getListOfAllNewBuildings(VillageEntity village);
}
