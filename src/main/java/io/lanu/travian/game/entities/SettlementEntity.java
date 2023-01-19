package io.lanu.travian.game.entities;

import io.lanu.travian.enums.EManipulation;
import io.lanu.travian.enums.ENation;
import io.lanu.travian.enums.SettlementSubType;
import io.lanu.travian.enums.SettlementType;
import io.lanu.travian.game.entities.events.ConstructionEventEntity;
import io.lanu.travian.game.models.battle.UnitsConst;
import io.lanu.travian.game.models.buildings.BuildingsConst;
import io.lanu.travian.game.models.buildings.BuildingsID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private String ownerUserName;
    private int x;
    private int y;
    private String name;
    private ENation nation;
    private int population;
    private int culture;
    private int approval;
    private Map<Integer, BuildModel> buildings;
    private List<BigDecimal> storage;
    private List<Integer> homeLegion;
    private List<ConstructionEventEntity> constructionEventList;
    private List<OrderCombatUnitEntity> combatUnitOrders;
    //@LastModifiedDate
    private LocalDateTime modifiedTime;

    public List<BigDecimal> calculateProducePerHour(){
        var res = buildings.values().stream()
                .filter(b -> b.getId().equals(BuildingsID.WOODCUTTER) || b.getId().equals(BuildingsID.CLAY_PIT) ||
                        b.getId().equals(BuildingsID.IRON_MINE) || b.getId().equals(BuildingsID.CROPLAND))
                .collect(Collectors.groupingBy(BuildModel::getId,
                                Collectors.reducing(BigDecimal.ZERO, b -> BigDecimal.valueOf(BuildingsConst.BUILDINGS.get(b.getId().ordinal()).getBenefit(b.getLevel())), BigDecimal::add)));
        List<BigDecimal> result = Arrays.asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        res.forEach((k, v) -> result.set(k.ordinal(), v));
        return result;
    }

    public BigDecimal calculateEatPerHour() {
        var eatExpenses = IntStream.range(0, 10)
                .mapToObj(i -> UnitsConst.UNITS.get(nation.ordinal()).get(i).getUpKeep() * homeLegion.get(i))
                .reduce(0, Integer::sum);
        return BigDecimal.valueOf(eatExpenses);
    }

    public void calculateProducedGoods(LocalDateTime lastModified, LocalDateTime untilTime){
        final MathContext mc = new MathContext(3);
        List<BigDecimal> producePerHour = calculateProducePerHour();

        long durationFromLastModified = ChronoUnit.MILLIS.between(lastModified, untilTime);

        // here is a formula for the productions counting
        // new BigDecimal((durationFromLastModified * (double) producePerHour.get(FieldType.WOOD)) / 3600000L, mc)
        BigDecimal divide = BigDecimal.valueOf(durationFromLastModified)
                .divide(BigDecimal.valueOf(3_600_000L), mc);

        BigDecimal woodProduced =
                producePerHour.get(0)
                        .multiply(divide);
        BigDecimal clayProduced =
                producePerHour.get(1)
                        .multiply(divide);
        BigDecimal ironProduced =
                producePerHour.get(2)
                        .multiply(divide);
        BigDecimal cropProduced =
                producePerHour.get(3)
                        .multiply(divide);

        manipulateGoods(EManipulation.ADD, Arrays.asList(woodProduced, clayProduced, ironProduced, cropProduced));
    }


    public void manipulateGoods(EManipulation kindOfManipulation, List<BigDecimal> goods){
        if (kindOfManipulation.equals(EManipulation.ADD)){
            for (int i = 0; i < storage.size(); i++){
                var res = storage.get(i);
                storage.set(i, res.add(goods.get(i)));
            }
        } else {
            for (int i = 0; i < storage.size(); i++){
                var res = storage.get(i);
                storage.set(i, res.subtract(goods.get(i)));
            }
        }
    }

    public void manipulateHomeLegion(List<Integer> units){
        for (int i = 0; i < homeLegion.size(); i++){
            homeLegion.set(i, homeLegion.get(i) + units.get(i));
        }
    }

    public BigDecimal getWarehouseCapacity() {
        double warehouse = buildings.values().stream()
                .filter(b -> b.getId().equals(BuildingsID.WAREHOUSE))
                .map(b -> BuildingsConst.BUILDINGS.get(b.getId().ordinal()).getBenefit(b.getLevel()))
                .reduce(0.0, Double::sum);
        return warehouse > 0.0 ? BigDecimal.valueOf(warehouse) : BigDecimal.valueOf(800);
    }

    public BigDecimal getGranaryCapacity() {
        double granary = buildings.values().stream()
                .filter(b -> b.getId().equals(BuildingsID.GRANARY))
                .map(b -> BuildingsConst.BUILDINGS.get(b.getId().ordinal()).getBenefit(b.getLevel()))
                .reduce(0.0, Double::sum);
        return granary > 0.0 ? BigDecimal.valueOf(granary) : BigDecimal.valueOf(800);
    }

    public void castStorage() {
        var warehouseCapacity = getWarehouseCapacity();
        var granaryCapacity = getGranaryCapacity();
        for (int i = 0; i < storage.size() - 1; i++) {
            if (storage.get(i).compareTo(warehouseCapacity) > 0){
                storage.set(i, warehouseCapacity);
            }
            // cast crop
            if (storage.get(3).compareTo(granaryCapacity) > 0){
                storage.set(3, granaryCapacity);
            }
        }
    }

    public void addPopulation(int amount){
        this.population += amount;
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
