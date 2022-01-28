package io.lanu.travian.game.services;

import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.*;
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
public class StateImpl implements IState{

    private static final MathContext mc = new MathContext(3);
    
    private final VillageService villageService;
    private final IConstructionService constructionService;
    private final MilitaryService militaryService;

    public StateImpl(VillageService villageService, IConstructionService constructionService, MilitaryService militaryService) {
        this.villageService = villageService;
        this.constructionService = constructionService;
        this.militaryService = militaryService;
    }

    @Override
    public VillageEntity getState(String villageId){
        return recalculateCurrentState(villageId);
    }

    @Override
    public VillageEntity saveState(VillageEntity village){
        return villageService.saveVillage(village);
    }

    private VillageEntity recalculateCurrentState(String villageId) {
        VillageEntity villageEntity = villageService.findById(villageId);
        var allEvents = combineAllEvents(villageId);
        executeAllEvents(villageEntity, allEvents);
        villageEntity.castStorage();
        return villageEntity;
    }

    private void executeAllEvents(VillageEntity villageEntity, List<IEvent> allEvents) {
        var modified = villageEntity.getModified();
        for (IEvent event : allEvents) {
            var cropPerHour = villageEntity.calculateProducePerHour().get(EResource.CROP);

            // if crop in the village is less than 0 keep create the death event & execute them until the crop will be positive
            while (cropPerHour.longValue() < 0) {
                var leftCrop = villageEntity.getStorage().get(EResource.CROP);
                var durationToDeath = leftCrop.divide(cropPerHour.negate(), mc).multiply(BigDecimal.valueOf(3_600_000), mc);

                LocalDateTime deathTime = modified.plus(durationToDeath.longValue(), ChronoUnit.MILLIS);

                if (deathTime.isBefore(event.getExecutionTime())) {
                    IEvent deathBuildEvent = new DeathEvent(deathTime);
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
            if (event instanceof MilitaryUnitDynamic){
                MilitaryUnitDynamic militaryEvent = (MilitaryUnitDynamic) event;
                var targetVillage = getState(militaryEvent.getTargetVillageId());
                militaryEvent.execute(targetVillage);
                saveState(targetVillage);
            }else {
                event.execute(villageEntity);
            }
            modified = event.getExecutionTime();
        }
    }

    private List<IEvent> combineAllEvents(String villageId) {
        List<IEvent> allEvents = new ArrayList<>();

        // add all building events
        allEvents.addAll(constructionService.findAllByVillageId(villageId));

        // add all units events
        allEvents.addAll(militaryService.createCombatUnitDoneEventsFromOrders(villageId));

        // add all wars events
        allEvents.addAll(militaryService.getAllByOriginVillageId(villageId));

        // add last empty event
        allEvents.add(new LastEvent(LocalDateTime.now()));

        return allEvents.stream()
                .filter(event -> event.getExecutionTime().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(IEvent::getExecutionTime))
                .collect(Collectors.toList());
    }

}
