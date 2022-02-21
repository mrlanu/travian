package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.ENation;
import io.lanu.travian.enums.SettlementSubType;
import io.lanu.travian.enums.SettlementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TileDetail {
    private String id;
    private SettlementType type;
    private SettlementSubType subType;
    private ENation nation;
    private String playerName;
    private String name;
    private int x;
    private int y;
    private int population;
    private BigDecimal distance;
}
