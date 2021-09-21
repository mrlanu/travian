package io.lanu.travian.game.services;

import io.lanu.travian.enums.Manipulation;
import io.lanu.travian.game.entities.FieldEntity;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.Event;
import io.lanu.travian.game.entities.events.FieldUpgradeEvent;
import io.lanu.travian.game.models.VillageManager;
import io.lanu.travian.game.models.requests.BuildingRequest;
import io.lanu.travian.game.models.responses.FieldView;
import io.lanu.travian.game.repositories.EventRepository;
import io.lanu.travian.game.repositories.VillageRepository;
import io.lanu.travian.templates.fields.FieldViewFactory;
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
    public Event createFieldUpgradeEvent(String villageId, Integer fieldPosition) {

        VillageEntity villageEntity = villageRepository.findById(villageId)
                .orElseThrow(() -> new IllegalStateException(String
                        .format("Village with id - %s is not exist.", villageId)));

        VillageManager villageManager = new VillageManager(villageEntity);
        FieldEntity fieldEntity = villageEntity.getFields().get(fieldPosition);
        FieldView fieldView = FieldViewFactory.get(fieldEntity.getType(), fieldEntity.getLevel());

        LocalDateTime executionTime = LocalDateTime.now()
                .plusSeconds(fieldView.getTimeToNextLevel());

        villageManager.manipulateGoods(Manipulation.SUBTRACT, fieldView.getResourcesToNextLevel());

        Event event = new FieldUpgradeEvent(executionTime, villageEntity.getVillageId(), fieldPosition);

        this.villageRepository.save(villageEntity);
        return this.eventRepository.save(event);
    }

    @Override
    public Event createBuildingNewEvent(String villageId, Integer buildingPosition, BuildingRequest buildingRequest) {
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
    public List<Event> findAllByVillageId(String villageId) {
        return eventRepository.findAllByVillageId(villageId);
    }

    @Override
    public void deleteByEventId(String eventId) {
        eventRepository.deleteByEventId(eventId);
    }
}
