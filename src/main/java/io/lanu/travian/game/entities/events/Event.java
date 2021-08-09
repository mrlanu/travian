package io.lanu.travian.game.entities.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.lanu.travian.enums.EventsType;
import io.lanu.travian.enums.Resource;
import io.lanu.travian.game.entities.VillageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Consumer;

@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FieldUpgradeEvent.class, name = "fieldUpgradeEvent"),
        @JsonSubTypes.Type(value = TroopEvent.class, name = "troopEvent")
})
@Document("events")
@TypeAlias("events")
public abstract class Event implements Consumer<VillageEntity> {

    @Id
    private String eventId;
    private EventsType type;
    private String villageId;
    private LocalDateTime executionTime;

    public Event(EventsType type, String villageId, LocalDateTime executionTime) {
        this.type = type;
        this.villageId = villageId;
        this.executionTime = executionTime;
    }

    public Event(EventsType type, LocalDateTime executionTime) {
        this.type = type;
        this.executionTime = executionTime;
    }

    void addGoodToProducePerHour(VillageEntity villageEntity, Resource resourceType, BigDecimal amount){
        var producePerHour = villageEntity.getProducePerHour();
        producePerHour.put(resourceType, producePerHour.get(resourceType).add(amount));
    }
}
