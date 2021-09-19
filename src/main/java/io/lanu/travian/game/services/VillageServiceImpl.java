package io.lanu.travian.game.services;

import io.lanu.travian.enums.BuildingType;
import io.lanu.travian.enums.Resource;
import io.lanu.travian.enums.VillageType;
import io.lanu.travian.game.entities.BuildingEntity;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.DeathEvent;
import io.lanu.travian.game.entities.events.Event;
import io.lanu.travian.game.models.VillageManager;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.VillageView;
import io.lanu.travian.game.repositories.VillageRepository;
import io.lanu.travian.templates.villages.VillageEntityFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VillageServiceImpl implements VillageService{
    private final VillageRepository villageRepository;
    private final EventService eventService;
    private static final MathContext mc = new MathContext(3);

    public VillageServiceImpl(VillageRepository villageRepository,
                              EventService eventService) {
        this.villageRepository = villageRepository;
        this.eventService = eventService;
    }

    @Override
    public VillageEntity createVillage(NewVillageRequest newVillageRequest) {
        VillageEntity newVillage = VillageEntityFactory.get(VillageType.SIX);
        newVillage.setAccountId(newVillageRequest.getAccountId());
        newVillage.setX(100);
        newVillage.setY(100);
        newVillage.setEventsList(new ArrayList<>());
        var defaultBuildings = setDefaultBuildings();
        newVillage.setBuildings(defaultBuildings);
        return villageRepository.save(newVillage);
    }

    private Map<Integer, BuildingEntity> setDefaultBuildings(){
        return Map.of(0, new BuildingEntity(BuildingType.MAIN, 1));
    }

    @Override
    public VillageView getVillageById(String villageId) {
        VillageEntity villageEntity = this.villageRepository.findById(villageId)
                .orElseThrow(() -> new IllegalStateException(String.format("Village with id - %s is not exist.", villageId)));
        VillageView result = build(villageEntity);
        this.villageRepository.save(villageEntity);
        return result;
    }

    private VillageView build(VillageEntity villageEntity){

        VillageManager villageManager = new VillageManager(villageEntity);

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
                    villageManager.calculateProducedGoods(modified, deathEvent.getExecutionTime());
                    deathEvent.accept(villageManager);
                    modified = deathEvent.getExecutionTime();
                } else {
                    break;
                }
                cropPerHour = villageEntity.getProducePerHour().get(Resource.CROP);
            }

            // recalculate storage leftovers
            villageManager.calculateProducedGoods(modified, event.getExecutionTime());
            event.accept(villageManager);
            this.eventService.deleteByEventId(event.getEventId());
            modified = event.getExecutionTime();
        }

        //villageEntity.setProducePerHour(sumProducePerHour());

        villageManager.calculateProducedGoods(villageEntity.getModified(), LocalDateTime.now());

        List<Event> allEvents = eventService.findAllByVillageId(villageEntity.getVillageId());
        villageManager.addEventsView(allEvents);

        return villageManager.getVillageView();
    }

}
