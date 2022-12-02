package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.EBuilding;
import io.lanu.travian.enums.ENation;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.enums.SettlementType;
import io.lanu.travian.game.entities.BuildModel;
import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.ConstructionEventEntity;
import io.lanu.travian.templates.buildings.BuildingBase;
import io.lanu.travian.templates.buildings.BuildingsFactory;
import io.lanu.travian.templates.military.CombatUnitFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VillageView {
    private String villageId;
    private String accountId;
    private String ownerUserName;
    private ENation nation;
    private String name;
    private int x;
    private int y;
    private SettlementType villageType;
    private int population;
    private int culture;
    private int approval;
    private List<BuildingBase> buildings;
    private Map<EResource, BigDecimal> storage;
    private BigDecimal warehouseCapacity;
    private BigDecimal granaryCapacity;
    private Map<String, Integer> homeLegion;
    private int[] homeUnits;
    private Map<EResource, BigDecimal> producePerHour;
    private List<ConstructionEventView> eventsList;
    private List<CombatUnitOrderView> unitOrders;

    public VillageView(SettlementEntity settlementEntity, List<ConstructionEventEntity> eventList, List<CombatGroupEntity> militariesInVillage) {
        createView(settlementEntity, eventList, militariesInVillage);
    }

    private void createView(SettlementEntity settlementEntity, List<ConstructionEventEntity> eventList, List<CombatGroupEntity> militariesInVillage) {
        this.villageId = settlementEntity.getId();
        this.accountId = settlementEntity.getAccountId();
        this.ownerUserName = settlementEntity.getOwnerUserName();
        this.nation = settlementEntity.getNation();
        this.name = settlementEntity.getName();
        this.x = settlementEntity.getX();
        this.y = settlementEntity.getY();
        this.villageType = settlementEntity.getSettlementType();
        this.population = settlementEntity.getPopulation();
        this.culture = settlementEntity.getCulture();
        this.approval = settlementEntity.getApproval();
        this.storage = settlementEntity.getStorage();
        this.warehouseCapacity = settlementEntity.getWarehouseCapacity();
        this.granaryCapacity = settlementEntity.getGranaryCapacity();
        this.buildings = this.buildBuildingsView(settlementEntity.getBuildings(), eventList);
        this.homeLegion = this.mapHomeLegion(settlementEntity.getHomeLegion(), settlementEntity.getNation(), militariesInVillage);
        this.homeUnits = settlementEntity.getHomeLegion();
        this.producePerHour = settlementEntity.calculateProducePerHour();
        this.eventsList = this.buildEventsView(eventList);
        this.unitOrders = this.buildUnitOrdersView(settlementEntity.getCombatUnitOrders());
    }

    private Map<String, Integer> mapHomeLegion(int[] homeLegion, ENation nation, List<CombatGroupEntity> militariesInVillage) {
        var result = new HashMap<String, Integer>();
        for (int i = 0; i < homeLegion.length; i++){
            result.put(CombatUnitFactory.getCombatUnitFromArrayPosition(i, nation).getName(), homeLegion[i]);
        }
        militariesInVillage.forEach(unit -> {
            for (int i = 0; i < unit.getUnits().length; i++){
                var key = CombatUnitFactory.getCombatUnitFromArrayPosition(i, nation).getName();
                result.put(key, result.getOrDefault(key, 0) + unit.getUnits()[i]);
            }
        });
        return result;
    }

    private List<ConstructionEventView> buildEventsView(List<ConstructionEventEntity> buildEventList) {
        /*DurationFormatUtils.formatDuration(Duration.between(LocalDateTime.now(),
                event.getExecutionTime()).toMillis(), "H:mm:ss", true)*/
        return buildEventList.stream()
                .map(event -> new ConstructionEventView(event.getEventId(), event.getBuildingPosition(), event.getBuildingName().getName(), event.getToLevel(), event.getExecutionTime(),
                        ChronoUnit.SECONDS.between(LocalDateTime.now(), event.getExecutionTime()))).collect(Collectors.toList());
    }

    private List<CombatUnitOrderView> buildUnitOrdersView(List<OrderCombatUnitEntity> orders) {
        return orders
                .stream()
                .sorted(Comparator.comparing(OrderCombatUnitEntity::getCreated))
                .map(order -> {
                    var duration = Duration.between(LocalDateTime.now(), order.getEndOrderTime()).toSeconds();
                    return new CombatUnitOrderView(
                            order.getUnitType().getName(),
                            order.getLeftTrain(),
                            duration,
                            order.getDurationEach(),
                            order.getEndOrderTime());
                })
                .collect(Collectors.toList());
    }
    
    private List<BuildingBase> buildBuildingsView(Map<Integer, BuildModel> buildings, List<ConstructionEventEntity> eventList) {
        return IntStream.range(1, 40)
                .mapToObj(i -> {
                    BuildingBase building = BuildingsFactory.getBuilding(buildings.get(i).getKind(), buildings.get(i).getLevel());
                    building.setPosition(i);
                    if (!building.getName().equals(EBuilding.EMPTY.getName())){
                        building.setAbleToUpgrade(this.storage);
                        building.setUnderUpgrade(eventList);
                        // if current building is already under upgrade resources needed for next level should be overwritten
                        if (building.isUnderUpgrade()){
                            var resources = buildings.get(i).getKind().getResourcesToNextLevel(building.getLevel() + 1);
                            building.setResourcesToNextLevel(resources);
                            building.setAbleToUpgrade(this.storage);
                        }
                    }
                    return building;
                })
                .collect(Collectors.toList());
    }
}
