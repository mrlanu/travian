package io.lanu.travian.game.models.requests;

import io.lanu.travian.enums.SettlementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewVillageRequest {
    private String accountId;
    private String ownerUserName;
    private SettlementType settlementType;
    private Integer x;
    private Integer y;
}
