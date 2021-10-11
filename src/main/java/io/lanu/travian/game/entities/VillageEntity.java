package io.lanu.travian.game.entities;

import io.lanu.travian.enums.EUnits;
import io.lanu.travian.enums.Manipulation;
import io.lanu.travian.enums.Resource;
import io.lanu.travian.enums.VillageType;
import io.lanu.travian.game.models.BuildModel;
import io.lanu.travian.game.models.responses.Field;
import io.lanu.travian.templates.buildings.BuildingBase;
import io.lanu.travian.templates.fields.FieldsFactory;
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
    private VillageType villageType;
    private int population;
    private int culture;
    private Map<Integer, BuildModel> buildings;
    private Map<Resource, BigDecimal> storage;
    private Map<EUnits, Integer> homeLegion;
    @LastModifiedDate
    private LocalDateTime modified;

    public Map<Resource, BigDecimal> calculateProducePerHour(){
        var result = IntStream.range(1, 6)
                .mapToObj(i -> FieldsFactory.get(buildings.get(i).getBuildingName(), buildings.get(i).getLevel()))
                .collect(Collectors.groupingBy(Field::getResource,
                        Collectors.reducing(BigDecimal.ZERO, Field::getProduction, BigDecimal::add)));
        result.put(Resource.CROP, result.get(Resource.CROP).subtract(calculateEatPerHour()));
        return result;
    }

    //dummy implementation
    private BigDecimal calculateEatPerHour() {
        BigDecimal result = BigDecimal.ZERO;
        return result.add(BigDecimal.valueOf(homeLegion.getOrDefault(EUnits.LEGIONNAIRE, 0)));
    }

    public Map<Integer, BuildingBase> mapBuildings(){
        return null;
    }

    public void calculateProducedGoods(LocalDateTime lastModified, LocalDateTime untilTime){
        final MathContext mc = new MathContext(3);
        Map<Resource, BigDecimal> producePerHour = calculateProducePerHour();

        long durationFromLastModified = ChronoUnit.MILLIS.between(lastModified, untilTime);

        // here is a formula for the productions counting
        // new BigDecimal((durationFromLastModified * (double) producePerHour.get(FieldType.WOOD)) / 3600000L, mc)
        BigDecimal divide = BigDecimal.valueOf(durationFromLastModified)
                .divide(BigDecimal.valueOf(3600000L), mc);

        BigDecimal woodProduced =
                producePerHour.get(Resource.WOOD)
                        .multiply(divide);
        BigDecimal clayProduced =
                producePerHour.get(Resource.CLAY)
                        .multiply(divide);
        BigDecimal ironProduced =
                producePerHour.get(Resource.IRON)
                        .multiply(divide);
        BigDecimal cropProduced =
                producePerHour.get(Resource.CROP)
                        .multiply(divide);

        manipulateGoods(Manipulation.ADD, Map.of(Resource.WOOD, woodProduced, Resource.CLAY, clayProduced,
                Resource.IRON, ironProduced, Resource.CROP, cropProduced));
    }


    public void manipulateGoods(Manipulation kindOfManipulation, Map<Resource, BigDecimal> goods){
        if (kindOfManipulation.equals(Manipulation.ADD)){
            storage.forEach((k, v) -> storage.put(k, storage.get(k).add(goods.get(k))));
        } else {
            storage.forEach((k, v) -> storage.put(k, storage.get(k).subtract(goods.get(k))));
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
