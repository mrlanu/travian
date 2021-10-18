package io.lanu.travian.game.services;

import io.lanu.travian.enums.EResource;
import io.lanu.travian.enums.EVillageType;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.BuildIEvent;
import io.lanu.travian.game.entities.events.DeathIEvent;
import io.lanu.travian.game.entities.events.IEvent;
import io.lanu.travian.game.entities.events.LastEvent;
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
import java.util.stream.Collectors;

@Service
public class VillageServiceImpl implements VillageService{
    private final VillageRepository villageRepository;
    private final EventService eventService;
    private final ArmiesService armiesService;
    private static final MathContext mc = new MathContext(3);

    public VillageServiceImpl(VillageRepository villageRepository,
                              EventService eventService, ArmiesService armiesService) {
        this.villageRepository = villageRepository;
        this.eventService = eventService;
        this.armiesService = armiesService;
    }

    @Override
    public VillageEntity createVillage(NewVillageRequest newVillageRequest) {
        VillageEntity newVillage = VillageEntityFactory.getVillageByType(EVillageType.SIX);
        newVillage.setAccountId(newVillageRequest.getAccountId());
        return villageRepository.save(newVillage);
    }

    @Override
    public VillageView getVillageById(String villageId) {
        VillageEntity villageEntity = this.villageRepository.findById(villageId)
                .orElseThrow(() -> new IllegalStateException(String.format("Village with id - %s is not exist.", villageId)));
        recalculateVillage(villageEntity);
        this.villageRepository.save(villageEntity);
        eventService.deleteAllByVillageIdAndExecutionTimeBefore(villageId, LocalDateTime.now());
        List<BuildIEvent> currentBuildingEvents = eventService.findAllByVillageId(villageId);
        return new VillageView(villageEntity, currentBuildingEvents);
    }

    private void recalculateVillage(VillageEntity villageEntity){

        List<IEvent> allEvents = combineAllEvents(villageEntity);
        LocalDateTime modified = villageEntity.getModified();

        // iterate over all events and execute them
        for (IEvent event : allEvents) {
            var cropPerHour = villageEntity.calculateProducePerHour().get(EResource.CROP);

            // if crop in the village is less than 0 keep create the death event & execute them until the crop will be positive
            while (cropPerHour.longValue() < 0) {
                var leftCrop = villageEntity.getStorage().get(EResource.CROP);
                var durationToDeath = leftCrop.divide(cropPerHour.negate(), mc).multiply(BigDecimal.valueOf(3_600_000), mc);

                LocalDateTime deathTime = modified.plus(durationToDeath.longValue(), ChronoUnit.MILLIS);

                if (deathTime.isBefore(event.getExecutionTime())) {
                    IEvent deathBuildEvent = new DeathIEvent(deathTime);
                    villageEntity.calculateProducedGoods(modified, deathBuildEvent.getExecutionTime());
                    deathBuildEvent.execute(villageEntity);
                    modified = deathBuildEvent.getExecutionTime();
                } else {
                    break;
                }
                cropPerHour = villageEntity.calculateProducePerHour().get(EResource.CROP);
            }
            // recalculate storage leftovers
            villageEntity.calculateProducedGoods(modified, event.getExecutionTime());
            event.execute(villageEntity);
            modified = event.getExecutionTime();
        }
        villageEntity.castStorage();
    }

    private List<IEvent> combineAllEvents(VillageEntity villageEntity) {
        List<IEvent> allEvents = new ArrayList<>();

        // add all building events
        allEvents.addAll(this.eventService.findAllByVillageId(villageEntity.getVillageId())
                .stream()
                .filter(event -> event.getExecutionTime().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(BuildIEvent::getExecutionTime))
                .collect(Collectors.toList()));

        // add all units events
        allEvents.addAll(this.armiesService.createTroopsBuildEventsFromOrders(villageEntity.getVillageId()));

        allEvents.add(new LastEvent(LocalDateTime.now()));

        return allEvents.stream()
                .filter(task -> task.getExecutionTime().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(IEvent::getExecutionTime))
                .collect(Collectors.toList());
    }

}
