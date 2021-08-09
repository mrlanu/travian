package io.lanu.travian.game.models.requests;

import lombok.Data;

@Data
public class FieldUpgradeRequest {
    private String villageId;
    private int fieldPosition;
}
