package io.lanu.travian.game.entities;

import io.lanu.travian.enums.Manipulation;
import io.lanu.travian.enums.Resource;
import io.lanu.travian.enums.VillageType;
import io.lanu.travian.game.entities.events.Event;
import io.lanu.travian.game.models.responses.EventView;
import io.lanu.travian.game.models.Field;
import io.lanu.travian.game.models.responses.FieldView;
import io.lanu.travian.game.models.responses.VillageView;
import io.lanu.travian.templates.buildings.BuildingBase;
import io.lanu.travian.templates.fields.FieldViewFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Document("villages")
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class VillageEntity {
    @Id
    protected String villageId;
    private String accountId;
    private int x;
    private int y;
    private VillageType villageType;
    private int population;
    private int culture;
    private List<FieldEntity> fields;
    private Map<Integer, BuildingEntity> buildings;
    private Map<Resource, BigDecimal> storage;
    private Map<Resource, BigDecimal> producePerHour;
    private List<EventView> eventsList;
    @LastModifiedDate
    private LocalDateTime modified;

    public Map<Resource, BigDecimal> calculateProducePerHour(){
        Map<Resource, BigDecimal> result = mapFields().stream()
                .collect(Collectors.groupingBy(FieldView::getFieldType,
                        Collectors.reducing(BigDecimal.ZERO, FieldView::getProduction, BigDecimal::add)));
        this.producePerHour = result;
        return result;
    }

    public List<FieldView> mapFields(){
        return this.fields.stream()
                .map(fieldEntity -> FieldViewFactory.get(fieldEntity.getType(), fieldEntity.getLevel())).collect(Collectors.toList());
    }

    public Map<Integer, BuildingBase> mapBuildings(){
        return null;
    }

    public void calculateProducedGoods(LocalDateTime lastModified, LocalDateTime untilTime){
        final MathContext mc = new MathContext(3);
        calculateProducePerHour();

        long durationFromLastModified = ChronoUnit.MILLIS.between(lastModified, untilTime);

        // here is a formula for the productions counting
        // new BigDecimal((durationFromLastModified * (double) producePerHour.get(FieldType.WOOD)) / 3600000L, mc)
        BigDecimal divide = BigDecimal.valueOf(durationFromLastModified)
                .divide(BigDecimal.valueOf(3600000L), mc);

        BigDecimal woodProduced =
                this.producePerHour.get(Resource.WOOD)
                        .multiply(divide);
        BigDecimal clayProduced =
                this.producePerHour.get(Resource.CLAY)
                        .multiply(divide);
        BigDecimal ironProduced =
                this.producePerHour.get(Resource.IRON)
                        .multiply(divide);
        BigDecimal cropProduced =
                this.producePerHour.get(Resource.CROP)
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

    public void addEventsView(List<Event> eventList){
        List<EventView> events = eventList.stream()
                .map(event -> new EventView(String.format("%s field upgrade", event.getType()), event.getExecutionTime(),
                        DurationFormatUtils.formatDuration(Duration.between(LocalDateTime.now(),
                                event.getExecutionTime()).toMillis(), "H:mm:ss", true)))
                .collect(Collectors.toList());
        this.eventsList.addAll(events);
    }

    public VillageView getVillageView() {
        return new VillageView(this);
    }
}
