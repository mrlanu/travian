package io.lanu.travian.game.models.requests;

import lombok.Data;

@Data
public class OrderCombatUnitRequest {
    private String villageId;
    private int unit;
    private Integer amount;
    private Integer level;
}
