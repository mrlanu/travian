package io.lanu.travian.game.models.requests;

import io.lanu.travian.enums.VillageType;
import lombok.Data;

@Data
public class NewVillageRequest {
    private String accountId;
    private VillageType villageType;
    private Integer x;
    private Integer y;
}
