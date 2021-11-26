package io.lanu.travian.game.models.requests;

import io.lanu.travian.enums.ECombatUnit;
import lombok.Data;

@Data
public class OrderCombatUnitRequest {
    private String villageId;
    private ECombatUnit unitType;
    private Integer amount;
    private Integer level;
}
