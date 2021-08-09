package io.lanu.travian.game.services;

import io.lanu.travian.enums.Resource;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.DeathEvent;
import io.lanu.travian.game.entities.events.Event;
import io.lanu.travian.game.repositories.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VillageGeneratorFacade {


    private final EventRepository eventRepository;
    private static final MathContext mc = new MathContext(3);

    public VillageGeneratorFacade(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void generateVillage(VillageEntity villageEntity){

        List<Event> eventList = eventRepository.findAllByVillageId(villageEntity.getVillageId())
                .stream()
                .filter(event -> event.getExecutionTime().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Event::getExecutionTime))
                .collect(Collectors.toList());

        LocalDateTime modified = villageEntity.getModified();

        // iterate over all events and execute them
        for (Event event : eventList) {
            var cropPerHour = villageEntity.getProducePerHour().get(Resource.CROP);

            // if crop in the village is less than 0 keep create the death event & execute them until the crop will be positive
            while (cropPerHour.longValue() < 0) {
                var leftCrop = villageEntity.getStorage().get(Resource.CROP);
                var durationToDeath = leftCrop.divide(cropPerHour.negate(), mc).multiply(BigDecimal.valueOf(3_600_000), mc);

                LocalDateTime deathTime = modified.plus(durationToDeath.longValue(), ChronoUnit.MILLIS);

                if (deathTime.isBefore(event.getExecutionTime())) {
                    Event deathEvent = new DeathEvent(deathTime);
                    calculateProducedGoods(villageEntity, modified, deathEvent.getExecutionTime());
                    deathEvent.accept(villageEntity);
                    modified = deathEvent.getExecutionTime();
                } else {
                    break;
                }
                cropPerHour = villageEntity.getProducePerHour().get(Resource.CROP);
            }

            // recalculate storage leftovers
            calculateProducedGoods(villageEntity, modified, event.getExecutionTime());
            event.accept(villageEntity);
            eventRepository.deleteByEventId(event.getEventId());
            modified = event.getExecutionTime();
        }

        /*villageEntity.setProducePerHour(sumProducePerHour());*/
        calculateProducedGoods(villageEntity, villageEntity.getModified(), LocalDateTime.now());
    }

    private void calculateProducedGoods(VillageEntity villageEntity, LocalDateTime lastModified, LocalDateTime untilTime){

        long durationFromLastModified = ChronoUnit.MILLIS.between(lastModified, untilTime);

        // here is a formula for the productions counting
        // new BigDecimal((durationFromLastModified * (double) producePerHour.get(FieldType.WOOD)) / 3600000L, mc)
        BigDecimal divide = BigDecimal.valueOf(durationFromLastModified)
                .divide(BigDecimal.valueOf(3600000L), mc);

        BigDecimal woodProduced =
                villageEntity.getProducePerHour().get(Resource.WOOD)
                        .multiply(divide);
        BigDecimal clayProduced =
                villageEntity.getProducePerHour().get(Resource.CLAY)
                        .multiply(divide);
        BigDecimal ironProduced =
                villageEntity.getProducePerHour().get(Resource.IRON)
                        .multiply(divide);
        BigDecimal cropProduced =
                villageEntity.getProducePerHour().get(Resource.CROP)
                        .multiply(divide);

        addGoods(villageEntity, Map.of(Resource.WOOD, woodProduced, Resource.CLAY, clayProduced,
                Resource.IRON, ironProduced, Resource.CROP, cropProduced));

        log.info("Produced resources added to the Warehouse.");
    }

    /*private Map<Resource, BigDecimal> sumProducePerHour(){
        return villageEntity.getFields().stream()
                .collect(Collectors.groupingBy(Field::getFieldType,
                        Collectors.reducing(BigDecimal.ZERO, Field::getProduction, BigDecimal::add)));
    }*/

    private void addGoods(VillageEntity villageEntity, Map<Resource, BigDecimal> producedResources){
        var storage = villageEntity.getStorage();
        storage.forEach((k, v) -> storage.put(k, storage.get(k).add(producedResources.get(k))));
    }

    public void addGoodToProducePerHour(VillageEntity villageEntity, Resource resourceType, BigDecimal amount){
        var producePerHour = villageEntity.getProducePerHour();
        producePerHour.put(resourceType, producePerHour.get(resourceType).add(amount));
    }

}
