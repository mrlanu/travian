package io.lanu.travian.game.services;

import io.lanu.travian.enums.Resource;
import io.lanu.travian.enums.VillageType;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.DeathEvent;
import io.lanu.travian.game.entities.events.Event;
import io.lanu.travian.game.models.VillageEntityWrapper;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.repositories.VillageRepository;
import io.lanu.travian.templates.entities.VillageTemplate;
import io.lanu.travian.templates.repositories.VillageTemplatesRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VillageServiceImpl implements VillageService{
    private final VillageRepository villageRepository;
    private final VillageTemplatesRepo villageTemplatesRepo;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private static final MathContext mc = new MathContext(3);

    public VillageServiceImpl(VillageRepository villageRepository,
                              VillageTemplatesRepo villageTemplatesRepo,
                              EventService eventService, ModelMapper modelMapper) {
        this.villageRepository = villageRepository;
        this.villageTemplatesRepo = villageTemplatesRepo;
        this.eventService = eventService;
        this.modelMapper = modelMapper;
    }

    @Override
    public VillageEntity createVillage(NewVillageRequest newVillageRequest) {
        VillageTemplate villageTemplate = villageTemplatesRepo.findByVillageType(VillageType.SIX);
        villageTemplate.setAccountId(newVillageRequest.getAccountId());
        villageTemplate.setX(100);
        villageTemplate.setY(100);
        villageTemplate.setPopulation(100);
        villageTemplate.setCulture(0);
        VillageEntity newVillage = modelMapper.map(villageTemplate, VillageEntity.class);
        newVillage.setEventsList(new ArrayList<>());
        return villageRepository.save(newVillage);
    }

    @Override
    public VillageEntity getVillageById(String villageId) {
        VillageEntity villageEntity = this.villageRepository.findById(villageId)
                .orElseThrow(() -> new IllegalStateException(String.format("Village with id - %s is not exist.", villageId)));
        recalculateVillage(villageEntity);
        return this.villageRepository.save(villageEntity);
    }

    private void recalculateVillage(VillageEntity villageEntity){

        VillageEntityWrapper villageEntityWrapper = new VillageEntityWrapper(villageEntity);

        List<Event> completedEvents = this.eventService.findAllByVillageId(villageEntity.getVillageId())
                .stream()
                .filter(event -> event.getExecutionTime().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Event::getExecutionTime))
                .collect(Collectors.toList());

        LocalDateTime modified = villageEntity.getModified();

        // iterate over all events and execute them
        for (Event event : completedEvents) {
            var cropPerHour = villageEntity.getProducePerHour().get(Resource.CROP);

            // if crop in the village is less than 0 keep create the death event & execute them until the crop will be positive
            while (cropPerHour.longValue() < 0) {
                var leftCrop = villageEntity.getStorage().get(Resource.CROP);
                var durationToDeath = leftCrop.divide(cropPerHour.negate(), mc).multiply(BigDecimal.valueOf(3_600_000), mc);

                LocalDateTime deathTime = modified.plus(durationToDeath.longValue(), ChronoUnit.MILLIS);

                if (deathTime.isBefore(event.getExecutionTime())) {
                    Event deathEvent = new DeathEvent(deathTime);
                    villageEntityWrapper.calculateProducedGoods(modified, deathEvent.getExecutionTime());
                    deathEvent.accept(villageEntityWrapper);
                    modified = deathEvent.getExecutionTime();
                } else {
                    break;
                }
                cropPerHour = villageEntity.getProducePerHour().get(Resource.CROP);
            }

            // recalculate storage leftovers
            villageEntityWrapper.calculateProducedGoods(modified, event.getExecutionTime());
            event.accept(villageEntityWrapper);
            this.eventService.deleteByEventId(event.getEventId());
            modified = event.getExecutionTime();
        }

        /*villageEntity.setProducePerHour(sumProducePerHour());*/
        villageEntityWrapper.calculateProducedGoods(villageEntity.getModified(), LocalDateTime.now());

        List<Event> allEvents = eventService.findAllByVillageId(villageEntity.getVillageId());
        villageEntityWrapper.addEventsView(allEvents);
    }

}
