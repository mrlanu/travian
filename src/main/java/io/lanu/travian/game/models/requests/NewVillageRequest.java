package io.lanu.travian.game.models.requests;

import io.lanu.travian.enums.EVillageType;
import lombok.Data;

@Data
public class NewVillageRequest {
    private String accountId;
    private EVillageType villageType;
    private Integer x;
    private Integer y;
}
