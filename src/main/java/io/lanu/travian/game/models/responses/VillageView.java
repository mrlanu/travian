package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.Resource;
import io.lanu.travian.enums.VillageType;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.templates.buildings.BuildingBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VillageView {
    private String villageId;
    private String accountId;
    private int x;
    private int y;
    private VillageType villageType;
    private int population;
    private int culture;
    private List<FieldView> fields;
    private Map<Integer, BuildingBase> buildings;
    private Map<Resource, BigDecimal> storage;
    private Map<Resource, BigDecimal> producePerHour;
    private List<EventView> eventsList;

    public VillageView(VillageEntity villageEntity) {
        this.villageId = villageEntity.getVillageId();
        this.accountId = villageEntity.getAccountId();
        this.x = villageEntity.getX();
        this.y = villageEntity.getY();
        this.villageType = villageEntity.getVillageType();
        this.population = villageEntity.getPopulation();
        this.culture = villageEntity.getCulture();
        this.fields = villageEntity.mapFields();
        this.buildings = villageEntity.mapBuildings();
        this.storage = villageEntity.getStorage();
        this.producePerHour = villageEntity.getProducePerHour();
        this.eventsList = villageEntity.getEventsList();
    }

}
