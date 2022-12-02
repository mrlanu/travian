package io.lanu.travian.game.entities;

import io.lanu.travian.enums.ECombatUnit;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class OrderCombatUnitEntity {
    private String orderId = UUID.randomUUID().toString();
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
