package io.lanu.travian.game.entities;

import io.lanu.travian.enums.ECombatUnit;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("combat-unit-orders")
@NoArgsConstructor
public class OrderCombatUnitEntity {
    @Id
    private String orderId;
    @CreatedDate
    private LocalDateTime created;
    private String villageId;
    private LocalDateTime lastTime;
    private ECombatUnit unitType;
    private Integer leftTrain;
    private Long durationEach;
    private int eatHour;
    private LocalDateTime endOrderTime;

    public OrderCombatUnitEntity(String villageId, LocalDateTime lastTime, ECombatUnit unitType,
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
