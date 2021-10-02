package io.lanu.travian.game.services;

import io.lanu.travian.enums.EventsType;
import io.lanu.travian.enums.Manipulation;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.BuildIEvent;
import io.lanu.travian.game.models.BuildModel;
import io.lanu.travian.game.models.requests.BuildingRequest;
import io.lanu.travian.game.models.responses.Field;
import io.lanu.travian.game.repositories.EventRepository;
import io.lanu.travian.game.repositories.VillageRepository;
import io.lanu.travian.templates.fields.FieldsFactory;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService{
    private final VillageRepository villageRepository;
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    public EventServiceImpl(VillageRepository villageRepository,
                            EventRepository eventRepository,
                            ModelMapper modelMapper) {
        this.villageRepository = villageRepository;
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BuildIEvent createBuildEvent(String villageId, Integer buildPosition) {

        VillageEntity villageEntity = villageRepository.findById(villageId)
                .orElseThrow(() -> new IllegalStateException(String
                        .format("Village with id - %s is not exist.", villageId)));

        BuildModel buildModel = villageEntity.getBuildings().get(buildPosition);
        Field fieldView = FieldsFactory.get(buildModel.getBuildingName(), buildModel.getLevel());

        LocalDateTime executionTime = LocalDateTime.now()
                .plusSeconds(fieldView.getTimeToNextLevel());

        villageEntity.manipulateGoods(Manipulation.SUBTRACT, fieldView.getResourcesToNextLevel());

        BuildIEvent buildEvent = new BuildIEvent(buildPosition, buildModel.getBuildingName(), EventsType.NEW_BUILDING, villageId, executionTime);

        this.villageRepository.save(villageEntity);
        return this.eventRepository.save(buildEvent);
    }

    @Override
    public BuildIEvent createBuildingNewEvent(String villageId, Integer buildingPosition, BuildingRequest buildingRequest) {
        /*VillageEntity villageEntity = villageRepository.findById(villageId)
                .orElseThrow(() -> new IllegalStateException(String
                        .format("Village with id - %s is not exist.", villageId)));

        BuildingBase building = buildingsRepository.findBuildingBaseByBuildingTypeAndLevel(
                buildingRequest.getBuildingType(), buildingRequest.getLevel())
                .orElseThrow(() -> new IllegalStateException(String
                        .format("Building with type - %s is not exist.", buildingRequest.getBuildingType())));

        VillageEntityWrapper villageEntityWrapper = new VillageEntityWrapper(villageEntity);
        villageEntityWrapper.manipulateGoods(Manipulation.SUBTRACT, building.getResourcesToNextLevel());

        LocalDateTime executionTime = LocalDateTime.now()
                .plusSeconds(building.getResourcesToNextLevel().get(Resource.TIME).longValue());

        Event event = new NewBuildingEvent(villageId, executionTime, building);
        this.villageRepository.save(villageEntity);
        return this.eventRepository.save(event);*/
        return null;
    }

    @Override
    public List<BuildIEvent> findAllByVillageId(String villageId) {
        return eventRepository.findAllByVillageId(villageId);
    }

    @Override
    public void deleteByEventId(String eventId) {
        eventRepository.deleteByEventId(eventId);
    }

    @Override
    public void deleteAllByVillageIdAndExecutionTimeBefore(String villageId, LocalDateTime time) {
        eventRepository.deleteAllByVillageIdAndExecutionTimeBefore(villageId, time);
    }
}
