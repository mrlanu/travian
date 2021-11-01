package io.lanu.travian.game.entities;

import io.lanu.travian.enums.EUnits;
import io.lanu.travian.enums.EManipulation;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.enums.EVillageType;
import io.lanu.travian.game.models.responses.FieldView;
import io.lanu.travian.templates.buildings.BuildingsFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@Document("villages")
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class VillageEntity {
    @Id
    protected String villageId;
    private String accountId;
    private String name;
    private int x;
    private int y;
    private EVillageType villageType;
    private int population;
    private int culture;
    private Map<Integer, BuildModel> buildings;
    private Map<EResource, BigDecimal> storage;
    private Map<EUnits, Integer> homeLegion;
    @LastModifiedDate
    private LocalDateTime modified;

    public Map<EResource, BigDecimal> calculateProducePerHour(){
        var result = IntStream.range(1, 19)
                .mapToObj(i -> BuildingsFactory.getField(buildings.get(i).getBuildingName(), buildings.get(i).getLevel()))
                .collect(Collectors.groupingBy(FieldView::getResource,
                        Collectors.reducing(BigDecimal.ZERO, FieldView::getProduction, BigDecimal::add)));
        result.put(EResource.CROP, result.get(EResource.CROP).subtract(calculateEatPerHour()));
        return result;
    }

    //dummy implementation
    private BigDecimal calculateEatPerHour() {
        BigDecimal result = BigDecimal.ZERO;
        return result.add(BigDecimal.valueOf(homeLegion.getOrDefault(EUnits.LEGIONNAIRE, 0)));
    }

    public void calculateProducedGoods(LocalDateTime lastModified, LocalDateTime untilTime){
        final MathContext mc = new MathContext(3);
        Map<EResource, BigDecimal> producePerHour = calculateProducePerHour();

        long durationFromLastModified = ChronoUnit.MILLIS.between(lastModified, untilTime);

        // here is a formula for the productions counting
        // new BigDecimal((durationFromLastModified * (double) producePerHour.get(FieldType.WOOD)) / 3600000L, mc)
        BigDecimal divide = BigDecimal.valueOf(durationFromLastModified)
                .divide(BigDecimal.valueOf(3_600_000L), mc);

        BigDecimal woodProduced =
                producePerHour.get(EResource.WOOD)
                        .multiply(divide);
        BigDecimal clayProduced =
                producePerHour.get(EResource.CLAY)
                        .multiply(divide);
        BigDecimal ironProduced =
                producePerHour.get(EResource.IRON)
                        .multiply(divide);
        BigDecimal cropProduced =
                producePerHour.get(EResource.CROP)
                        .multiply(divide);

        manipulateGoods(EManipulation.ADD, Map.of(EResource.WOOD, woodProduced, EResource.CLAY, clayProduced,
                EResource.IRON, ironProduced, EResource.CROP, cropProduced));
    }


    public void manipulateGoods(EManipulation kindOfManipulation, Map<EResource, BigDecimal> goods){
        if (kindOfManipulation.equals(EManipulation.ADD)){
            storage.forEach((k, v) -> storage.put(k, storage.get(k).add(goods.get(k))));
        } else {
            storage.forEach((k, v) -> storage.put(k, storage.get(k).subtract(goods.get(k))));
        }
    }

    public BigDecimal getWarehouseCapacity() {
        return BigDecimal.valueOf(800);
    }

    public BigDecimal getGranaryCapacity() {
        return BigDecimal.valueOf(800);
    }

    public void castStorage() {
        for (Map.Entry<EResource, BigDecimal> entry : this.storage.entrySet()) {
            if (entry.getKey().equals(EResource.CROP)){
                if (entry.getValue().compareTo(getGranaryCapacity()) > 0){
                    this.storage.put(entry.getKey(), getGranaryCapacity());
                }
                continue;
            }
            if (entry.getValue().compareTo(getWarehouseCapacity()) > 0){
                this.storage.put(entry.getKey(), getWarehouseCapacity());
            }
        }
    }

    /*public void addGoodToProducePerHour(Resource resourceType, BigDecimal amount){
        var producePerHour = villageEntity.getProducePerHour();
        producePerHour.put(resourceType, producePerHour.get(resourceType).add(amount));
    }*/

    /*public void addEventsView(List<Event> eventList){
        List<EventView> events = eventList.stream()
                .map(event -> new EventView(String.format("%s field upgrade", event.getType()), event.getExecutionTime(),
                        DurationFormatUtils.formatDuration(Duration.between(LocalDateTime.now(),
                                event.getExecutionTime()).toMillis(), "H:mm:ss", true)))
                .collect(Collectors.toList());
        this.eventsList.addAll(events);
    }*/
}
