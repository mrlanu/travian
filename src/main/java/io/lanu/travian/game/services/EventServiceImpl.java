package io.lanu.travian.game.services;

import io.lanu.travian.enums.Resource;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.Event;
import io.lanu.travian.game.entities.events.FieldUpgradeEvent;
import io.lanu.travian.game.models.Field;
import io.lanu.travian.game.models.requests.FieldUpgradeRequest;
import io.lanu.travian.game.repositories.EventRepository;
import io.lanu.travian.game.repositories.VillageRepository;
import io.lanu.travian.templates.entities.FieldTemplate;
import io.lanu.travian.templates.repositories.FieldTemplatesRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class EventServiceImpl implements EventService{
    private final VillageRepository villageRepository;
    private final EventRepository eventRepository;
    private final FieldTemplatesRepository fieldTemplatesRepository;
    private final ModelMapper modelMapper;

    public EventServiceImpl(VillageRepository villageRepository,
                            EventRepository eventRepository,
                            FieldTemplatesRepository fieldTemplatesRepository,
                            ModelMapper modelMapper) {
        this.villageRepository = villageRepository;
        this.eventRepository = eventRepository;
        this.fieldTemplatesRepository = fieldTemplatesRepository;

        this.modelMapper = modelMapper;
    }

    @Override
    public Event createFieldUpgradeEvent(FieldUpgradeRequest fieldUpgradeRequest) {
        VillageEntity village = villageRepository.findById(fieldUpgradeRequest.getVillageId()).get();
        Field oldField = village.getFields().get(fieldUpgradeRequest.getFieldPosition());
        oldField.setUnderUpgrade(true);
        FieldTemplate template = fieldTemplatesRepository
                .findByFieldTypeAndLevel(oldField.getFieldType(), oldField.getLevel() + 1);
        Field newField = modelMapper.map(template, Field.class);
        newField.setPosition(oldField.getPosition());
        LocalDateTime executionTime = LocalDateTime.now()
                .plusSeconds(oldField.getResourcesToNextLevel().get(Resource.TIME).longValue());

        //here should be implemented functionality for payment for the field upgrade

        FieldUpgradeEvent event = new FieldUpgradeEvent(executionTime, village.getVillageId(), newField, oldField, false);

        subtractGoods(village, oldField.getResourcesToNextLevel());
        villageRepository.save(village);

        return eventRepository.save(event);
    }

    private void subtractGoods(VillageEntity villageEntity, Map<Resource, BigDecimal> payedGoods){
        var storage = villageEntity.getStorage();
        storage.forEach((k, v) -> storage.put(k, storage.get(k).subtract(payedGoods.get(k))));
    }
}
