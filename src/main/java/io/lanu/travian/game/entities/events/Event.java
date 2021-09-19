package io.lanu.travian.game.entities.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.lanu.travian.enums.EventsType;
import io.lanu.travian.game.models.VillageManager;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

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
public abstract class Event implements Consumer<VillageManager> {

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
}
