package io.lanu.travian.game.entities;

import io.lanu.travian.enums.EUnits;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("army-orders")
@TypeAlias("army-orders")
@NoArgsConstructor
public class ArmyOrderEntity {
    @Id
    private String orderId;
    @CreatedDate
    private LocalDateTime created;
    private String villageId;
    private LocalDateTime lastTime;
    private EUnits unitType;
    private Integer leftTrain;
    private Long durationEach;
    private int eatHour;
    private LocalDateTime endOrderTime;

    public ArmyOrderEntity(String villageId, LocalDateTime lastTime, EUnits unitType,
                           Integer leftTrain, Long durationEach, int eatHour, LocalDateTime endOrderTime) {
        this.villageId = villageId;
        this.lastTime = lastTime;
        this.unitType = unitType;
        this.leftTrain = leftTrain;
        this.durationEach = durationEach;
        this.eatHour = eatHour;
        this.endOrderTime = endOrderTime;
    }
}
