package io.lanu.travian.game.models;

import io.lanu.travian.enums.Manipulation;
import io.lanu.travian.enums.Resource;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.Event;
import io.lanu.travian.game.models.responses.EventView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class VillageEntityWrapper {

    private final VillageEntity villageEntity;
    private static final MathContext mc = new MathContext(3);

    public VillageEntityWrapper(VillageEntity villageEntity) {
        this.villageEntity = villageEntity;
    }

    public VillageEntity getVillageEntity() {
        return villageEntity;
    }

    public void calculateProducedGoods(LocalDateTime lastModified, LocalDateTime untilTime){

        long durationFromLastModified = ChronoUnit.MILLIS.between(lastModified, untilTime);

        // here is a formula for the productions counting
        // new BigDecimal((durationFromLastModified * (double) producePerHour.get(FieldType.WOOD)) / 3600000L, mc)
        BigDecimal divide = BigDecimal.valueOf(durationFromLastModified)
                .divide(BigDecimal.valueOf(3600000L), mc);

        BigDecimal woodProduced =
                this.villageEntity.getProducePerHour().get(Resource.WOOD)
                        .multiply(divide);
        BigDecimal clayProduced =
                this.villageEntity.getProducePerHour().get(Resource.CLAY)
                        .multiply(divide);
        BigDecimal ironProduced =
                this.villageEntity.getProducePerHour().get(Resource.IRON)
                        .multiply(divide);
        BigDecimal cropProduced =
                this.villageEntity.getProducePerHour().get(Resource.CROP)
                        .multiply(divide);

        manipulateGoods(Manipulation.ADD, Map.of(Resource.WOOD, woodProduced, Resource.CLAY, clayProduced,
                Resource.IRON, ironProduced, Resource.CROP, cropProduced));
    }

    public void addGoodToProducePerHour(Resource resourceType, BigDecimal amount){
        var producePerHour = villageEntity.getProducePerHour();
        producePerHour.put(resourceType, producePerHour.get(resourceType).add(amount));
    }

    public void manipulateGoods(Manipulation kindOfManipulation, Map<Resource, BigDecimal> goods){
        var storage = this.villageEntity.getStorage();
        if (kindOfManipulation.equals(Manipulation.ADD)){
            storage.forEach((k, v) -> storage.put(k, storage.get(k).add(goods.get(k))));
        } else {
            storage.forEach((k, v) -> storage.put(k, storage.get(k).subtract(goods.get(k))));
        }
    }

    public void addEventsView(List<Event> eventList){
        List<EventView> events = eventList.stream()
                .map(event -> new EventView(String.format("%s field upgrade", event.getType()), event.getExecutionTime(),
                        DurationFormatUtils.formatDuration(Duration.between(LocalDateTime.now(),
                                event.getExecutionTime()).toMillis(), "H:mm:ss", true)))
                .collect(Collectors.toList());
        this.villageEntity.setEventsList(events);
    }
}
