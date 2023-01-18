package io.lanu.travian.game.services;

import io.lanu.travian.enums.EBuildingType;
import io.lanu.travian.enums.EManipulation;
import io.lanu.travian.game.dto.SettlementStateDTO;
import io.lanu.travian.game.entities.BuildModel;
import io.lanu.travian.game.entities.events.ConstructionEventEntity;
import io.lanu.travian.game.models.buildings.BuildingsConst;
import io.lanu.travian.game.models.buildings.BuildingsID;
import io.lanu.travian.game.models.responses.NewBuilding;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConstructionServiceImpl implements ConstructionService {

    private final EngineService engineService;

    public ConstructionServiceImpl(EngineService engineService) {
        this.engineService = engineService;
    }

    @Override
    public SettlementStateDTO createBuildEvent(String settlementId, Integer buildingPosition, BuildingsID buildingID) {
        var currentState = engineService.updateParticularSettlementState(settlementId, LocalDateTime.now());
        var events = currentState.getSettlementEntity().getConstructionEventList()
                .stream()
                .sorted(Comparator.comparing(ConstructionEventEntity::getExecutionTime))
                .collect(Collectors.toList());

        // if current building is already under upgrade resources & time needed for next level should be overwritten
        var alreadyUnderUpgrade = events.stream()
                .anyMatch(constructionEvent -> constructionEvent.getBuildingPosition() == buildingPosition);

        BuildModel buildModel;
        //the kind of building if requested new building, and null if requested upgrade
        if (buildingID != null) {
            buildModel = new BuildModel(buildingID, 0);
            currentState.getSettlementEntity().getBuildings().put(buildingPosition, buildModel);
        } else {
            buildModel = currentState.getSettlementEntity().getBuildings().get(buildingPosition);
        }

        var buildingBlueprint = BuildingsConst.BUILDINGS.get(buildModel.getId().ordinal());
        var toLevel = alreadyUnderUpgrade ? buildModel.getLevel() + 2 : buildModel.getLevel() + 1;

        LocalDateTime executionTime = events.size() > 0 ?
                events.get(events.size() - 1).getExecutionTime().plusSeconds(buildingBlueprint.getTime().valueOf(toLevel)) :
                LocalDateTime.now().plusSeconds(buildingBlueprint.getTime().valueOf(toLevel));

        currentState.getSettlementEntity().manipulateGoods(EManipulation.SUBTRACT, buildingBlueprint.getResourcesToNextLevel(toLevel));

        ConstructionEventEntity buildEvent = new ConstructionEventEntity(buildingPosition, buildModel.getId(),
                toLevel, currentState.getSettlementEntity().getId(), executionTime);

        currentState.getSettlementEntity().getConstructionEventList().add(buildEvent);
        return engineService.saveSettlementEntity(currentState);
    }

    @Override
    public SettlementStateDTO deleteBuildingEvent(String settlementId, String eventId) {
        var currentState = engineService.updateParticularSettlementState(settlementId,LocalDateTime.now());
        var allEvents = currentState.getSettlementEntity().getConstructionEventList().stream()
                .sorted(Comparator.comparing(ConstructionEventEntity::getExecutionTime))
                .collect(Collectors.toList());
        var event = allEvents.stream()
                .filter(constructionEvent -> constructionEvent.getEventId().equals(eventId))
                .findFirst()
                .orElseThrow();
        var numberOfEvents = allEvents.stream()
                .filter(e -> e.getBuildingPosition() == event.getBuildingPosition())
                .count();

        BuildModel buildModel = currentState.getSettlementEntity().getBuildings().get(event.getBuildingPosition());
        var buildingBlueprint = BuildingsConst.BUILDINGS.get(buildModel.getId().ordinal());
        var toLevel = numberOfEvents == 1 ? buildModel.getLevel() + 1: buildModel.getLevel() + 2;
        //deduct time that was needed for building from following events
        var events = allEvents.stream()
                .filter(e -> e.getExecutionTime().isAfter(event.getExecutionTime()))
                .collect(Collectors.toList());
        events.forEach(e -> {
            e.setExecutionTime(e.getExecutionTime().minusSeconds(buildingBlueprint.getTime().valueOf(toLevel)));
            //decrement level by one if we have previous events for this building
            if (numberOfEvents > 1) {
                e.setToLevel(e.getToLevel() - 1);
            }
        });
        // if deleting any building construction to level 1. in this case should return empty spot (except resources fields)
        if (event.getToLevel() == 1 && event.getBuildingPosition() >= 19 && numberOfEvents == 1){
            currentState.getSettlementEntity().getBuildings().put(event.getBuildingPosition(), new BuildModel(BuildingsID.EMPTY, 0));
        }
        currentState.getSettlementEntity().manipulateGoods(EManipulation.ADD, buildingBlueprint.getResourcesToNextLevel(toLevel));
        currentState.getSettlementEntity().getConstructionEventList().remove(event);
        return engineService.saveSettlementEntity(currentState);
    }

    @Override
    public List<NewBuilding> getListOfAllNewBuildings(String settlementId) {
        var currentState = engineService.updateParticularSettlementState(settlementId, LocalDateTime.now());
        var events = currentState.getSettlementEntity().getConstructionEventList();
        var all = getListOfNewBuildings();
        // if events size >=2 return all buildings unavailable for build otherwise checking ability to build
        return events.size() >= 2 ? all : all.stream()
                .filter(newBuilding -> newBuilding.isBuildingExistAndMaxLevelAndMulti(currentState.getSettlementEntity().getBuildings()))
                .peek(newBuilding -> newBuilding.checkAvailability(currentState.getSettlementEntity().getBuildings().values(),
                        currentState.getSettlementEntity().getStorage()))
                .collect(Collectors.toList());
    }

    private List<NewBuilding> getListOfNewBuildings(){
        var result = new ArrayList<NewBuilding>();
        BuildingsConst.BUILDINGS.forEach(b -> {
            if (!b.getType().equals(EBuildingType.RESOURCE) && !b.getType().equals(EBuildingType.EMPTY)){
                result.add(
                        NewBuilding.builder()
                                .name(b.getName())
                                .buildingID(b.getId())
                                .type(b.getType())
                                .description(b.getDescription())
                                .cost(b.getResourcesToNextLevel(1))
                                .time(b.getTime().valueOf(1))
                                .requirements(b.getRequirementBuildings())
                                .maxLevel(b.getMaxLevel())
                                .multi(b.isMulti())
                                .available(false)
                                .build());
            }
        });
        return result;
    }
}
