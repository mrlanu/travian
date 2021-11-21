package io.lanu.travian.game.models.requests;

import io.lanu.travian.enums.EUnits;
import lombok.Data;

@Data
public class ArmyOrderRequest {
    private String villageId;
    private EUnits unitType;
    private Integer amount;
    private Integer level;
}
