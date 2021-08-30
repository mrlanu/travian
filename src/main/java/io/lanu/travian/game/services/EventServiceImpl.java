package io.lanu.travian.game.services;

import io.lanu.travian.enums.Manipulation;
import io.lanu.travian.enums.Resource;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.Event;
import io.lanu.travian.game.entities.events.FieldUpgradeEvent;
import io.lanu.travian.game.entities.events.NewBuildingEvent;
import io.lanu.travian.game.models.Field;
import io.lanu.travian.game.models.VillageEntityWrapper;
import io.lanu.travian.game.models.requests.BuildingRequest;
import io.lanu.travian.game.repositories.EventRepository;
import io.lanu.travian.game.repositories.VillageRepository;
import io.lanu.travian.templates.entities.FieldTemplate;
import io.lanu.travian.templates.entities.buildings.BuildingBase;
import io.lanu.travian.templates.repositories.BuildingsRepository;
import io.lanu.travian.templates.repositories.FieldTemplatesRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService{
    private final VillageRepository villageRepository;
    private final EventRepository eventRepository;
    private final FieldTemplatesRepository fieldTemplatesRepository;
    private final BuildingsRepository buildingsRepository;
    private final ModelMapper modelMapper;

    public EventServiceImpl(VillageRepository villageRepository,
                            EventRepository eventRepository,
                            FieldTemplatesRepository fieldTemplatesRepository,
                            BuildingsRepository buildingsRepository,
                            ModelMapper modelMapper) {
        this.villageRepository = villageRepository;
        this.eventRepository = eventRepository;
        this.fieldTemplatesRepository = fieldTemplatesRepository;
        this.buildingsRepository = buildingsRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Event createFieldUpgradeEvent(String villageId, Integer fieldPosition) {

        VillageEntity villageEntity = villageRepository.findById(villageId)
                .orElseThrow(() -> new IllegalStateException(String
                        .format("Village with id - %s is not exist.", villageId)));

        VillageEntityWrapper villageEntityWrapper = new VillageEntityWrapper(villageEntity);

        Field oldField = villageEntity.getFields().get(fieldPosition);
        oldField.setUnderUpgrade(true);
        FieldTemplate template = this.fieldTemplatesRepository
                .findByFieldTypeAndLevel(oldField.getFieldType(), oldField.getLevel() + 1);
        Field newField = this.modelMapper.map(template, Field.class);
        newField.setPosition(oldField.getPosition());
        LocalDateTime executionTime = LocalDateTime.now()
                .plusSeconds(oldField.getResourcesToNextLevel().get(Resource.TIME).longValue());

        villageEntityWrapper.manipulateGoods(Manipulation.SUBTRACT, oldField.getResourcesToNextLevel());

        Event event = new FieldUpgradeEvent(executionTime, villageEntity.getVillageId(), newField, oldField);

        this.villageRepository.save(villageEntity);

        return this.eventRepository.save(event);
    }

    @Override
    public Event createBuildingNewEvent(String villageId, Integer buildingPosition, BuildingRequest buildingRequest) {
        VillageEntity villageEntity = villageRepository.findById(villageId)
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
        return this.eventRepository.save(event);
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
