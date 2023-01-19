package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.ECombatGroupLocation;
import io.lanu.travian.enums.ENation;
import io.lanu.travian.enums.SettlementType;
import io.lanu.travian.game.dto.SettlementStateDTO;
import io.lanu.travian.game.entities.BuildModel;
import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.entities.events.ConstructionEventEntity;
import io.lanu.travian.game.models.battle.UnitsConst;
import io.lanu.travian.game.models.buildings.BuildingView;
import io.lanu.travian.game.models.buildings.BuildingsConst;
import io.lanu.travian.game.models.buildings.BuildingsID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SettlementView {
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
    private List<BuildingView> buildings;
    private List<BigDecimal> storage;
    private BigDecimal warehouseCapacity;
    private BigDecimal granaryCapacity;
    private Map<String, Integer> homeLegion;
    private List<Integer> homeUnits;
    private List<BigDecimal> producePerHour;
    private List<ConstructionEventView> eventsList;
    private List<CombatUnitOrderView> unitOrders;
    private Map<String, TroopMovementsBrief> movementsBrief;
    private Map<ECombatGroupLocation, List<CombatGroupView>> combatGroupByLocation;

    public SettlementView(SettlementStateDTO currentState) {
        var settlementEntity = currentState.getSettlementEntity();

        this.movementsBrief = currentState.getMovementsBriefMap();
        this.combatGroupByLocation = currentState.getCombatGroupByLocationMap();
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
        this.buildings = this.buildBuildingsView(settlementEntity.getBuildings(), settlementEntity.getConstructionEventList());
        this.homeLegion = this.mapHomeLegion(settlementEntity.getHomeLegion(), settlementEntity.getNation(),
                currentState.getCombatGroupsInSettlement());
        this.homeUnits = settlementEntity.getHomeLegion();
        this.producePerHour = settlementEntity.calculateProducePerHour();
        this.eventsList = this.buildEventsView(settlementEntity.getConstructionEventList());
        this.unitOrders = this.buildUnitOrdersView(settlementEntity.getCombatUnitOrders(), currentState.getSettlementEntity().getNation());
    }

    private Map<String, Integer> mapHomeLegion(List<Integer> homeLegion, ENation nation, List<CombatGroupEntity> militariesInVillage) {
        var result = new HashMap<String, Integer>();
        for (int i = 0; i < homeLegion.size(); i++){
            result.put(UnitsConst.UNITS.get(nation.ordinal()).get(i).getName(), homeLegion.get(i));
        }
        militariesInVillage.forEach(unit -> {
            for (int i = 0; i < unit.getUnits().size(); i++){
                var key = UnitsConst.UNITS.get(unit.getOwnerNation().ordinal()).get(i).getName();
                result.put(key, result.getOrDefault(key, 0) + unit.getUnits().get(i));
            }
        });
        return result;
    }

    private List<ConstructionEventView> buildEventsView(List<ConstructionEventEntity> buildEventList) {
        /*DurationFormatUtils.formatDuration(Duration.between(LocalDateTime.now(),
                event.getExecutionTime()).toMillis(), "H:mm:ss", true)*/
        return buildEventList.stream()
                .map(event -> new ConstructionEventView(event.getEventId(), event.getBuildingPosition(),
                        BuildingsConst.BUILDINGS.get(event.getBuildingID().ordinal()).getName(), event.getToLevel(), event.getExecutionTime(),
                        ChronoUnit.SECONDS.between(LocalDateTime.now(), event.getExecutionTime()))).collect(Collectors.toList());
    }

    private List<CombatUnitOrderView> buildUnitOrdersView(List<OrderCombatUnitEntity> orders, ENation nation) {
        return orders
                .stream()
                .sorted(Comparator.comparing(OrderCombatUnitEntity::getCreated))
                .map(order -> {
                    var duration = Duration.between(LocalDateTime.now(), order.getEndOrderTime()).toSeconds();
                    return new CombatUnitOrderView(
                            UnitsConst.UNITS.get(nation.ordinal()).get(order.getUnit()).getName(),
                            order.getLeftTrain(),
                            duration,
                            order.getDurationEach(),
                            order.getEndOrderTime());
                })
                .collect(Collectors.toList());
    }
    
    private List<BuildingView> buildBuildingsView(Map<Integer, BuildModel> buildings, List<ConstructionEventEntity> eventList) {
        return IntStream.range(1, 40)
                .mapToObj(i -> {
                    if (buildings.get(i).getId().equals(BuildingsID.EMPTY)){
                        return BuildingView.builder()
                                .name("empty-spot")
                                .position(i)
                                .description("Empty")
                                .build();
                    }
                    var bBlueprint = BuildingsConst.BUILDINGS.get(buildings.get(i).getId().ordinal());
                    var building = BuildingView.builder()
                            .name(bBlueprint.getName())
                            .level(buildings.get(i).getLevel())
                            .resourcesToNextLevel(bBlueprint.getResourcesToNextLevel(buildings.get(i).getLevel() + 1))
                            .maxLevel(bBlueprint.getMaxLevel())
                            .description(bBlueprint.getDescription())
                            .timeToNextLevel(bBlueprint.getTime().valueOf(buildings.get(i).getLevel() + 1))
                            .position(i)
                            .build();
                    building.setAbleToUpgrade(storage);
                    building.setUnderUpgrade(eventList);
                        // if current building is already under upgrade resources needed for next level should be overwritten
                    if (building.isUnderUpgrade()){
                        var resources = bBlueprint.getResourcesToNextLevel(building.getLevel() + 2);
                        var time = bBlueprint.getTime().valueOf(buildings.get(i).getLevel() + 2);
                        building.setResourcesToNextLevel(resources);
                        building.setAbleToUpgrade(this.storage);
                        building.setTimeToNextLevel(time);
                    }
                    return building;
                })
                .collect(Collectors.toList());
    }
}
