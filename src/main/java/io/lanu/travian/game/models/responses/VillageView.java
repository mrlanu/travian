package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.EUnits;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.enums.EVillageType;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.BuildIEvent;
import io.lanu.travian.game.entities.BuildModel;
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
    private String name;
    private int x;
    private int y;
    private EVillageType villageType;
    private int population;
    private int culture;
    private List<FieldView> fields;
    private Map<Integer, BuildingBase> buildings;
    private Map<EResource, BigDecimal> storage;
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
        this.fields = this.buildFieldsView(villageEntity.getBuildings(), eventList);
        this.buildings = villageEntity.mapBuildings();
        this.homeLegion = villageEntity.getHomeLegion();
        this.producePerHour = villageEntity.calculateProducePerHour();
        this.eventsList = this.buildEventsView(eventList);
    }

    private List<EventView> buildEventsView(List<BuildIEvent> buildEventList) {
        return buildEventList.stream()
                .map(event -> new EventView(event.getBuildingName().getName(), event.getExecutionTime(),
                        DurationFormatUtils.formatDuration(Duration.between(LocalDateTime.now(),
                        event.getExecutionTime()).toMillis(), "H:mm:ss", true))).collect(Collectors.toList());
    }

    private List<FieldView> buildFieldsView(Map<Integer, BuildModel> buildings, List<BuildIEvent> eventList) {
        return IntStream.range(1, 6)
                .mapToObj(i -> {
                    FieldView field = FieldsFactory.get(buildings.get(i).getBuildingName(), buildings.get(i).getLevel());
                    field.setPosition(i);
                    return field;
                })
                .peek(field -> {
                    field.setAbleToUpgrade(this.storage);
                    field.setUnderUpgrade(eventList);
                })
                .collect(Collectors.toList());
    }

}
