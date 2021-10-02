package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.Resource;
import io.lanu.travian.enums.VillageType;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.BuildIEvent;
import io.lanu.travian.game.models.BuildModel;
import io.lanu.travian.templates.buildings.BuildingBase;
import io.lanu.travian.templates.fields.FieldsFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
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

    public VillageView(VillageEntity villageEntity, List<BuildIEvent> eventList) {
        this.villageId = villageEntity.getVillageId();
        this.accountId = villageEntity.getAccountId();
        this.x = villageEntity.getX();
        this.y = villageEntity.getY();
        this.villageType = villageEntity.getVillageType();
        this.population = villageEntity.getPopulation();
        this.culture = villageEntity.getCulture();
        this.fields = this.buildFieldsView(villageEntity.getBuildings());
        this.buildings = villageEntity.mapBuildings();
        this.storage = villageEntity.getStorage();
        this.producePerHour = villageEntity.calculateProducePerHour();
        this.eventsList = this.buildEventsView(eventList);
    }

    private List<EventView> buildEventsView(List<BuildIEvent> buildEventList) {
        return buildEventList.stream()
                .map(event -> new EventView(event.getBuildingName().getName(), event.getExecutionTime(),
                        DurationFormatUtils.formatDuration(Duration.between(LocalDateTime.now(),
                        event.getExecutionTime()).toMillis(), "H:mm:ss", true))).collect(Collectors.toList());
    }

    private List<FieldView> buildFieldsView(Map<Integer, BuildModel> buildings) {
        return IntStream.range(1, 6)
                .mapToObj(i -> FieldsFactory.get(buildings.get(i).getBuildingName(), buildings.get(i).getLevel()))
                .collect(Collectors.toList());
    }

}
