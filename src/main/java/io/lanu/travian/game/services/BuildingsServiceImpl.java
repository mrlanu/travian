package io.lanu.travian.game.services;

import io.lanu.travian.enums.EBuildingType;
import io.lanu.travian.enums.EBuildings;
import io.lanu.travian.enums.EManipulation;
import io.lanu.travian.game.entities.BuildModel;
import io.lanu.travian.game.entities.events.ConstructionEvent;
import io.lanu.travian.game.models.responses.NewBuilding;
import io.lanu.travian.game.repositories.ConstructionEventsRepository;
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
public class BuildingsServiceImpl implements BuildingsService {
    private final VillageService villageService;
    private final ConstructionEventsRepository constructionEventsRepository;

    public BuildingsServiceImpl(VillageService villageService, ConstructionEventsRepository constructionEventsRepository) {
        this.villageService = villageService;
        this.constructionEventsRepository = constructionEventsRepository;
    }

    @Override
    public ConstructionEvent createBuildEvent(String villageId, Integer buildingPosition, EBuildings kind) {

        var villageEntity = villageService.recalculateVillage(villageId);
        var events = constructionEventsRepository.findAllByVillageId(villageId)
                .stream()
                .sorted(Comparator.comparing(ConstructionEvent::getExecutionTime))
                .collect(Collectors.toList());

        BuildModel buildModel;
        //will get a kind if requested new building and null if requested upgrade
        if (kind != null) {
            buildModel = new BuildModel(kind, 0);
            villageEntity.getBuildings().put(buildingPosition, buildModel);
        } else {
            buildModel = villageEntity.getBuildings().get(buildingPosition);
        }
        BuildingBase building = BuildingsFactory.getBuilding(buildModel.getKind(), buildModel.getLevel());

        LocalDateTime executionTime = events.size() > 0 ?
                events.get(events.size() - 1).getExecutionTime().plusSeconds(building.getTimeToNextLevel()) :
                LocalDateTime.now().plusSeconds(building.getTimeToNextLevel());

        villageEntity.manipulateGoods(EManipulation.SUBTRACT, building.getResourcesToNextLevel());

        ConstructionEvent buildEvent = new ConstructionEvent(buildingPosition, buildModel.getKind(), building.getLevel() + 1, villageId, executionTime);

        villageService.saveVillage(villageEntity);
        return this.constructionEventsRepository.save(buildEvent);
    }

    @Override
    public List<ConstructionEvent> findAllByVillageId(String villageId) {
        return constructionEventsRepository.findAllByVillageId(villageId);
    }

    @Override
    public void deleteByEventId(String eventId) {
        var event = constructionEventsRepository.findBuildIEventByEventId(eventId);
        var villageEntity = villageService.recalculateVillage(event.getVillageId());
        BuildModel buildModel = villageEntity.getBuildings().get(event.getBuildingPosition());
        BuildingBase field = BuildingsFactory.getBuilding(buildModel.getKind(), buildModel.getLevel());
        var events = constructionEventsRepository.findAllByVillageId(villageEntity.getVillageId())
                .stream()
                .sorted(Comparator.comparing(ConstructionEvent::getExecutionTime))
                .filter(e -> e.getExecutionTime().isAfter(event.getExecutionTime()))
                .collect(Collectors.toList());
        events.forEach(e -> e.setExecutionTime(e.getExecutionTime().minusSeconds(field.getTimeToNextLevel())));
        villageEntity.manipulateGoods(EManipulation.ADD, field.getResourcesToNextLevel());
        villageService.saveVillage(villageEntity);
        constructionEventsRepository.deleteByEventId(eventId);
        constructionEventsRepository.saveAll(events);
    }

    @Override
    public void deleteAllByVillageIdAndExecutionTimeBefore(String villageId, LocalDateTime time) {
        constructionEventsRepository.deleteAllByVillageIdAndExecutionTimeBefore(villageId, time);
    }


    @Override
    public List<NewBuilding> getListOfAllNewBuildings(String villageId) {
        var villageEntity = this.villageService.recalculateVillage(villageId);
        var events = constructionEventsRepository.findAllByVillageId(villageEntity.getVillageId());
        var all = getListOfNewBuildings();
        // if events size >=2 return all buildings unavailable for build otherwise checking ability to build
        return events.size() >= 2 ? all : all.stream()
                .filter(newBuilding -> newBuilding.isBuildingExistAndMaxLevelAndMulti(villageEntity.getBuildings()))
                .peek(newBuilding -> newBuilding.checkAvailability(villageEntity.getBuildings().values(), villageEntity.getStorage()))
                .collect(Collectors.toList());
    }

    private List<NewBuilding> getListOfNewBuildings(){
        var result = new ArrayList<NewBuilding>();
        Arrays.asList(EBuildings.values()).forEach(b -> {
            if (!b.getType().equals(EBuildingType.RESOURCE) && !b.getType().equals(EBuildingType.EMPTY)){
                var temp = BuildingsFactory.getBuilding(b, 0);
                result.add(new NewBuilding(b.getName(), b, b.getType(), b.getDescription(), temp.getResourcesToNextLevel(),
                        temp.getTimeToNextLevel(), b.getRequirementBuildings(), b.getMaxLevel(), false, b.isMulti()));
            }
        });
        return result;
    }
}
