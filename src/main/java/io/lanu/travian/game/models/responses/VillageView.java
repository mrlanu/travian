package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.EResource;
import io.lanu.travian.enums.EUnits;
import io.lanu.travian.enums.EVillageType;
import io.lanu.travian.game.entities.BuildModel;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.BuildIEvent;
import io.lanu.travian.templates.buildings.BuildingBase;
import io.lanu.travian.templates.buildings.BuildingsFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VillageView {
    private String villageId;
    private String accountId;
    private String name;
    private int x;
    private int y;
    private EVillageType villageType;
    private int population;
    private int culture;
    private List<FieldView> fields;
    private List<BuildingBase> buildings;
    private Map<EResource, BigDecimal> storage;
    private BigDecimal warehouseCapacity;
    private BigDecimal granaryCapacity;
    private Map<EUnits, Integer> homeLegion;
    private Map<EResource, BigDecimal> producePerHour;
    private List<EventView> eventsList;

    public VillageView(VillageEntity villageEntity, List<BuildIEvent> eventList) {
        this.villageId = villageEntity.getVillageId();
        this.accountId = villageEntity.getAccountId();
        this.name = villageEntity.getName();
        this.x = villageEntity.getX();
        this.y = villageEntity.getY();
        this.villageType = villageEntity.getVillageType();
        this.population = villageEntity.getPopulation();
        this.culture = villageEntity.getCulture();
        this.storage = villageEntity.getStorage();
        this.warehouseCapacity = villageEntity.getWarehouseCapacity();
        this.granaryCapacity = villageEntity.getGranaryCapacity();
        this.fields = this.buildFieldsView(villageEntity.getBuildings(), eventList);
        this.buildings = this.buildBuildingsView(villageEntity.getBuildings(), eventList);
        this.homeLegion = villageEntity.getHomeLegion();
        this.producePerHour = villageEntity.calculateProducePerHour();
        this.eventsList = this.buildEventsView(eventList);
    }

    private List<EventView> buildEventsView(List<BuildIEvent> buildEventList) {
        /*DurationFormatUtils.formatDuration(Duration.between(LocalDateTime.now(),
                event.getExecutionTime()).toMillis(), "H:mm:ss", true)*/
        return buildEventList.stream()
                .map(event -> new EventView(event.getEventId(), event.getBuildingName().getName(), event.getToLevel(), event.getExecutionTime(),
                        ChronoUnit.SECONDS.between(LocalDateTime.now(), event.getExecutionTime()))).collect(Collectors.toList());
    }

    private List<FieldView> buildFieldsView(Map<Integer, BuildModel> buildings, List<BuildIEvent> eventList) {
        return IntStream.range(1, 19)
                .mapToObj(i -> {
                    FieldView field = BuildingsFactory.getField(buildings.get(i).getBuildingName(), buildings.get(i).getLevel());
                    field.setPosition(i);
                    return field;
                })
                .peek(field -> {
                    field.setAbleToUpgrade(this.storage);
                    field.setUnderUpgrade(eventList);
                })
                .collect(Collectors.toList());
    }
    
    private List<BuildingBase> buildBuildingsView(Map<Integer, BuildModel> buildings, List<BuildIEvent> eventList) {
        return IntStream.range(19, 40)
                .mapToObj(i -> {
                    BuildingBase building = BuildingsFactory.getBuilding(buildings.get(i).getBuildingName(), buildings.get(i).getLevel());
                    building.setPosition(i);
                    return building;
                })
                .peek(building -> {
                    /*building.setAbleToUpgrade(this.storage);
                    building.setUnderUpgrade(eventList);*/
                })
                .collect(Collectors.toList());
    }
}
