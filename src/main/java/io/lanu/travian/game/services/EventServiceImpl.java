package io.lanu.travian.game.services;

import io.lanu.travian.enums.EBuildings;
import io.lanu.travian.enums.EManipulation;
import io.lanu.travian.game.entities.BuildModel;
import io.lanu.travian.game.entities.events.BuildIEvent;
import io.lanu.travian.game.repositories.EventRepository;
import io.lanu.travian.game.repositories.VillageRepository;
import io.lanu.travian.templates.buildings.BuildingBase;
import io.lanu.travian.templates.buildings.BuildingsFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService{
    private final VillageRepository villageRepository;
    private final EventRepository eventRepository;

    public EventServiceImpl(VillageRepository villageRepository,
                            EventRepository eventRepository) {
        this.villageRepository = villageRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public BuildIEvent createBuildEvent(String villageId, Integer buildingPosition, EBuildings kind) {

        var villageEntity = villageRepository.findById(villageId)
                .orElseThrow(() -> new IllegalStateException(String
                        .format("Village with id - %s is not exist.", villageId)));

        var events = eventRepository.findAllByVillageId(villageId)
                .stream()
                .sorted(Comparator.comparing(BuildIEvent::getExecutionTime))
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

        BuildIEvent buildEvent = new BuildIEvent(buildingPosition, buildModel.getKind(), building.getLevel() + 1, villageId, executionTime);

        this.villageRepository.save(villageEntity);
        return this.eventRepository.save(buildEvent);
    }

    @Override
    public List<BuildIEvent> findAllByVillageId(String villageId) {
        return eventRepository.findAllByVillageId(villageId);
    }

    @Override
    public void deleteByEventId(String eventId) {
        var event = eventRepository.findBuildIEventByEventId(eventId);
        var villageEntity = villageRepository.findById(event.getVillageId())
                .orElseThrow(() -> new IllegalStateException(String
                .format("Village with id - %s is not exist.", event.getVillageId())));
        BuildModel buildModel = villageEntity.getBuildings().get(event.getBuildingPosition());
        BuildingBase field = BuildingsFactory.getBuilding(buildModel.getKind(), buildModel.getLevel());
        var events = eventRepository.findAllByVillageId(villageEntity.getVillageId())
                .stream()
                .sorted(Comparator.comparing(BuildIEvent::getExecutionTime))
                .filter(e -> e.getExecutionTime().isAfter(event.getExecutionTime()))
                .collect(Collectors.toList());
        events.forEach(e -> e.setExecutionTime(e.getExecutionTime().minusSeconds(field.getTimeToNextLevel())));
        villageEntity.manipulateGoods(EManipulation.ADD, field.getResourcesToNextLevel());
        this.villageRepository.save(villageEntity);
        eventRepository.deleteByEventId(eventId);
        eventRepository.saveAll(events);
    }

    @Override
    public void deleteAllByVillageIdAndExecutionTimeBefore(String villageId, LocalDateTime time) {
        eventRepository.deleteAllByVillageIdAndExecutionTimeBefore(villageId, time);
    }
}
