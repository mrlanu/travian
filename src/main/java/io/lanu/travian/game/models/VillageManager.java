package io.lanu.travian.game.models;

import io.lanu.travian.enums.Manipulation;
import io.lanu.travian.enums.Resource;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.Event;
import io.lanu.travian.game.models.responses.EventView;
import io.lanu.travian.game.models.responses.FieldView;
import io.lanu.travian.game.models.responses.VillageView;
import io.lanu.travian.templates.fields.FieldViewFactory;
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
public class VillageManager {

    private final VillageEntity villageEntity;
    private final VillageView villageView;
    private static final MathContext mc = new MathContext(3);

    public VillageManager(VillageEntity villageEntity) {
        this.villageEntity = villageEntity;
        this.villageView = new VillageView();
        this.mapVillage();

    }

    private void mapVillage(){

        villageView.setVillageId(villageEntity.getVillageId());
        villageView.setAccountId(villageEntity.getAccountId());
        villageView.setX(villageEntity.getX());
        villageView.setY(villageEntity.getY());
        villageView.setVillageType(villageEntity.getVillageType());
        villageView.setPopulation(villageEntity.getPopulation());
        villageView.setCulture(villageEntity.getCulture());
        villageView.setStorage(villageEntity.getStorage());

        villageView.setFields(mapFields());
        //villageView.setBuildings(mapBuildings());
        villageView.setStorage(mapStorage());
        villageView.setProducePerHour(mapProducePerHour());
        //villageView.setEventsList(mapEvents());

    }

    private List<FieldView> mapFields(){
        return this.villageEntity.getFields().stream()
                .map(fieldEntity -> FieldViewFactory.get(fieldEntity.getType(), fieldEntity.getLevel())).collect(Collectors.toList());
    }

    private Map<Resource, BigDecimal> mapStorage(){
        return this.villageEntity.getStorage();
    }

    private Map<Resource, BigDecimal> mapProducePerHour(){
        return sumProducePerHour();
    }

    private Map<Resource, BigDecimal> sumProducePerHour(){
        return this.villageView.getFields().stream()
                .collect(Collectors.groupingBy(FieldView::getFieldType,
                        Collectors.reducing(BigDecimal.ZERO, FieldView::getProduction, BigDecimal::add)));
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
                this.villageView.getProducePerHour().get(Resource.WOOD)
                        .multiply(divide);
        BigDecimal clayProduced =
                this.villageView.getProducePerHour().get(Resource.CLAY)
                        .multiply(divide);
        BigDecimal ironProduced =
                this.villageView.getProducePerHour().get(Resource.IRON)
                        .multiply(divide);
        BigDecimal cropProduced =
                this.villageView.getProducePerHour().get(Resource.CROP)
                        .multiply(divide);

        manipulateGoods(Manipulation.ADD, Map.of(Resource.WOOD, woodProduced, Resource.CLAY, clayProduced,
                Resource.IRON, ironProduced, Resource.CROP, cropProduced));
    }

    public void addGoodToProducePerHour(Resource resourceType, BigDecimal amount){
        var producePerHour = villageEntity.getProducePerHour();
        producePerHour.put(resourceType, producePerHour.get(resourceType).add(amount));
    }

    public void manipulateGoods(Manipulation kindOfManipulation, Map<Resource, BigDecimal> goods){
        var storage = this.villageView.getStorage();
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

    public VillageView getVillageView() {
        return this.villageView;
    }
}
