package io.lanu.travian.game.services;

import io.lanu.travian.enums.EBuildingType;
import io.lanu.travian.enums.EBuilding;
import io.lanu.travian.enums.EManipulation;
import io.lanu.travian.game.entities.BuildModel;
import io.lanu.travian.game.entities.events.ConstructionEvent;
import io.lanu.travian.game.models.responses.NewBuilding;
import io.lanu.travian.game.repositories.ConstructionEventRepository;
import io.lanu.travian.templates.buildings.BuildingBase;
import io.lanu.travian.templates.buildings.BuildingsFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuildingServiceImpl implements BuildingService {
    private final VillageService villageService;
    private final ConstructionEventRepository constructionEventRepository;

    public BuildingServiceImpl(VillageService villageService, ConstructionEventRepository constructionEventRepository) {
        this.villageService = villageService;
        this.constructionEventRepository = constructionEventRepository;
    }

    @Override
    public ConstructionEvent createBuildEvent(String villageId, Integer buildingPosition, EBuilding kind) {

        var villageEntity = villageService.recalculateVillage(villageId);
        var events = constructionEventRepository.findAllByVillageId(villageId)
                .stream()
                .sorted(Comparator.comparing(ConstructionEvent::getExecutionTime))
                .collect(Collectors.toList());

        // if current building is already under upgrade resources & time needed for next level should be overwritten
        var alreadyUnderUpgrade = events.stream()
                .anyMatch(constructionEvent -> constructionEvent.getBuildingPosition() == buildingPosition);

        BuildModel buildModel;
        //the kind of building if requested new building, and null if requested upgrade
        if (kind != null) {
            buildModel = new BuildModel(kind, 0);
            villageEntity.getBuildings().put(buildingPosition, buildModel);
        } else {
            buildModel = villageEntity.getBuildings().get(buildingPosition);
        }
        BuildingBase building = BuildingsFactory.getBuilding(buildModel.getKind(),
                alreadyUnderUpgrade ? buildModel.getLevel() + 1 : buildModel.getLevel());

        LocalDateTime executionTime = events.size() > 0 ?
                events.get(events.size() - 1).getExecutionTime().plusSeconds(building.getTimeToNextLevel()) :
                LocalDateTime.now().plusSeconds(building.getTimeToNextLevel());

        villageEntity.manipulateGoods(EManipulation.SUBTRACT, building.getResourcesToNextLevel());

        ConstructionEvent buildEvent = new ConstructionEvent(buildingPosition, buildModel.getKind(),
                building.getLevel() + 1, villageId, executionTime);

        villageService.saveVillage(villageEntity);
        return this.constructionEventRepository.save(buildEvent);
    }

    @Override
    public List<ConstructionEvent> findAllByVillageId(String villageId) {
        return constructionEventRepository.findAllByVillageId(villageId);
    }

    @Override
    public void deleteBuildingEvent(String villageId, String eventId) {
        var allEvents = constructionEventRepository.findAllByVillageId(villageId).stream()
                .sorted(Comparator.comparing(ConstructionEvent::getExecutionTime))
                .collect(Collectors.toList());
        var event = allEvents.stream()
                .filter(constructionEvent -> constructionEvent.getEventId().equals(eventId))
                .findFirst()
                .orElseThrow();
        var numberOfEvents = allEvents.stream()
                .filter(e -> e.getBuildingPosition() == event.getBuildingPosition())
                .count();

        var villageEntity = villageService.recalculateVillage(event.getVillageId());
        BuildModel buildModel = villageEntity.getBuildings().get(event.getBuildingPosition());
        BuildingBase building = BuildingsFactory.getBuilding(buildModel.getKind(),
                numberOfEvents == 1 ? buildModel.getLevel() : buildModel.getLevel() + 1);
        //deduct time that was needed for building from following events
        var events = allEvents.stream()
                .filter(e -> e.getExecutionTime().isAfter(event.getExecutionTime()))
                .collect(Collectors.toList());
        events.forEach(e -> {
            e.setExecutionTime(e.getExecutionTime().minusSeconds(building.getTimeToNextLevel()));
            //decrement level by one if we have previous events for this building
            if (numberOfEvents > 1) {
                e.setToLevel(e.getToLevel() - 1);
            }
        });
        // if deleting any building construction to level 1. in this case should return empty spot (except resources fields)
        if (event.getToLevel() == 1 && event.getBuildingPosition() >= 19 && numberOfEvents == 1){
            villageEntity.getBuildings().put(event.getBuildingPosition(), new BuildModel(EBuilding.EMPTY, 0));
        }
        villageEntity.manipulateGoods(EManipulation.ADD, building.getResourcesToNextLevel());
        villageService.saveVillage(villageEntity);
        constructionEventRepository.deleteByEventId(eventId);
        constructionEventRepository.saveAll(events);
    }

    @Override
    public void deleteAllByVillageIdAndExecutionTimeBefore(String villageId, LocalDateTime time) {
        constructionEventRepository.deleteAllByVillageIdAndExecutionTimeBefore(villageId, time);
    }


    @Override
    public List<NewBuilding> getListOfAllNewBuildings(String villageId) {
        var villageEntity = this.villageService.recalculateVillage(villageId);
        var events = constructionEventRepository.findAllByVillageId(villageEntity.getVillageId());
        var all = getListOfNewBuildings();
        // if events size >=2 return all buildings unavailable for build otherwise checking ability to build
        return events.size() >= 2 ? all : all.stream()
                .filter(newBuilding -> newBuilding.isBuildingExistAndMaxLevelAndMulti(villageEntity.getBuildings()))
                .peek(newBuilding -> newBuilding.checkAvailability(villageEntity.getBuildings().values(), villageEntity.getStorage()))
                .collect(Collectors.toList());
    }

    private List<NewBuilding> getListOfNewBuildings(){
        var result = new ArrayList<NewBuilding>();
        Arrays.asList(EBuilding.values()).forEach(b -> {
            if (!b.getType().equals(EBuildingType.RESOURCE) && !b.getType().equals(EBuildingType.EMPTY)){
                var temp = BuildingsFactory.getBuilding(b, 0);
                result.add(new NewBuilding(b.getName(), b, b.getType(), b.getDescription(), temp.getResourcesToNextLevel(),
                        temp.getTimeToNextLevel(), b.getRequirementBuildings(), b.getMaxLevel(), false, b.isMulti()));
            }
        });
        return result;
    }
}
