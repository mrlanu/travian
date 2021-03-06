package io.lanu.travian.game.entities;

import io.lanu.travian.enums.*;
import io.lanu.travian.templates.buildings.BuildingBase;
import io.lanu.travian.templates.buildings.BuildingsFactory;
import io.lanu.travian.templates.buildings.GranaryBuilding;
import io.lanu.travian.templates.buildings.WarehouseBuilding;
import io.lanu.travian.templates.military.CombatUnitFactory;
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
public class SettlementEntity {
    @Id
    protected String id;
    private SettlementType settlementType;
    private SettlementSubType subType;
    private String accountId;
    private int x;
    private int y;
    private String name;
    private ENation nation;
    private int population;
    private int culture;
    private int approval;
    private Map<Integer, BuildModel> buildings;
    private Map<EResource, BigDecimal> storage;
    private int[] homeLegion;
    @LastModifiedDate
    private LocalDateTime modified;

    public Map<EResource, BigDecimal> calculateProducePerHour(){
        var result = IntStream.range(1, 19)
                .mapToObj(i -> BuildingsFactory.getBuilding(buildings.get(i).getKind(), buildings.get(i).getLevel()))
                .collect(Collectors.groupingBy(BuildingBase::getResource,
                        Collectors.reducing(BigDecimal.ZERO, BuildingBase::getProduction, BigDecimal::add)));
        result.put(EResource.CROP, result.get(EResource.CROP).subtract(calculateEatPerHour()));
        return result;
    }

    public BigDecimal calculateEatPerHour() {
        var eatExpenses = IntStream.range(0, 10)
                .mapToObj(i -> CombatUnitFactory.getCombatUnitFromArrayPosition(i, ENation.GALLS).getEat() * homeLegion[i])
                .reduce(0, Integer::sum);
        return BigDecimal.valueOf(eatExpenses);
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
        // in BuildModel overridden equals method so level doesnt matter in containsValue
        return buildings.containsValue(new BuildModel(EBuilding.WAREHOUSE, 0)) ?
                buildings.values()
                    .stream()
                    .filter(buildModel -> buildModel.getKind().equals(EBuilding.WAREHOUSE))
                    .map(buildModel -> (WarehouseBuilding) BuildingsFactory.getBuilding(buildModel.getKind(), buildModel.getLevel()))
                    .map(WarehouseBuilding::getCapacity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.valueOf(800);
    }

    public BigDecimal getGranaryCapacity() {
        return buildings.containsValue(new BuildModel(EBuilding.GRANARY, 0)) ?
                buildings.values()
                    .stream()
                    .filter(buildModel -> buildModel.getKind().equals(EBuilding.GRANARY))
                    .map(buildModel -> (GranaryBuilding) BuildingsFactory.getBuilding(buildModel.getKind(), buildModel.getLevel()))
                    .map(GranaryBuilding::getCapacity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.valueOf(800);
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
