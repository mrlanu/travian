package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EBuildings;
import io.lanu.travian.enums.EventsType;
import io.lanu.travian.game.entities.VillageEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
/*@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FieldUpgradeBuildEvent.class, name = "fieldUpgradeEvent"),
        @JsonSubTypes.Type(value = TroopBuildEvent.class, name = "troopEvent")
})*/
@Document("build-events")
@TypeAlias("build-events")
public class BuildIEvent implements IEvent {

    @Id
    private String eventId;
    private int buildingPosition;
    private EBuildings buildingName;
    private EventsType type;
    private String villageId;
    private LocalDateTime executionTime;

    public BuildIEvent(int buildingPosition, EBuildings buildingName, EventsType type, String villageId, LocalDateTime executionTime) {
        this.buildingPosition = buildingPosition;
        this.buildingName = buildingName;
        this.type = type;
        this.villageId = villageId;
        this.executionTime = executionTime;
    }

    @Override
    public void execute(VillageEntity villageEntity) {
        //var field = villageEntity.getFields().get(fieldPosition);
        //field.setLevel(field.getLevel() + 1);

        //villageEntityWrapper.getVillageEntity().getFields().set(fieldNew.getPosition(), fieldNew);
        //villageEntityWrapper.addGoodToProducePerHour(fieldNew.getFieldType(), fieldNew.getProduction().subtract(fieldOld.getProduction()));
    }
}
