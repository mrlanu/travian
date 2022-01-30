package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.EBuilding;
import io.lanu.travian.enums.ENation;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.enums.EVillageType;
import io.lanu.travian.game.entities.BuildModel;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.ConstructionEventEntity;
import io.lanu.travian.game.entities.events.MilitaryUnitEntity;
import io.lanu.travian.templates.buildings.BuildingBase;
import io.lanu.travian.templates.buildings.BuildingsFactory;
import io.lanu.travian.templates.military.CombatUnitFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private ENation nation;
    private String name;
    private int x;
    private int y;
    private EVillageType villageType;
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
    private List<MilitaryUnitEntity> militariesInVillage;

    public VillageView(VillageEntity villageEntity, List<ConstructionEventEntity> eventList,
                       List<MilitaryUnitEntity> militariesInVillage) {
        this.villageId = villageEntity.getVillageId();
        this.accountId = villageEntity.getAccountId();
        this.nation = villageEntity.getNation();
        this.name = villageEntity.getName();
        this.x = villageEntity.getX();
        this.y = villageEntity.getY();
        this.villageType = villageEntity.getVillageType();
        this.population = villageEntity.getPopulation();
        this.culture = villageEntity.getCulture();
        this.approval = villageEntity.getApproval();
        this.storage = villageEntity.getStorage();
        this.warehouseCapacity = villageEntity.getWarehouseCapacity();
        this.granaryCapacity = villageEntity.getGranaryCapacity();
        this.buildings = this.buildBuildingsView(villageEntity.getBuildings(), eventList);
        this.homeLegion = this.mapHomeLegion(villageEntity.getHomeLegion(), villageEntity.getNation());
        this.homeUnits = villageEntity.getHomeLegion();
        this.producePerHour = villageEntity.calculateProducePerHour();
        this.eventsList = this.buildEventsView(eventList);
        this.militariesInVillage = militariesInVillage;
    }

    private Map<String, Integer> mapHomeLegion(int[] homeLegion, ENation nation) {
        var result = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < homeLegion.length; i++){
            result.put(CombatUnitFactory.getCombatUnitFromArrayPosition(i, nation).getName(), homeLegion[i]);
        }
        return result;
    }

    private List<ConstructionEventView> buildEventsView(List<ConstructionEventEntity> buildEventList) {
        /*DurationFormatUtils.formatDuration(Duration.between(LocalDateTime.now(),
                event.getExecutionTime()).toMillis(), "H:mm:ss", true)*/
        return buildEventList.stream()
                .map(event -> new ConstructionEventView(event.getEventId(), event.getBuildingPosition(), event.getBuildingName().getName(), event.getToLevel(), event.getExecutionTime(),
                        ChronoUnit.SECONDS.between(LocalDateTime.now(), event.getExecutionTime()))).collect(Collectors.toList());
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
