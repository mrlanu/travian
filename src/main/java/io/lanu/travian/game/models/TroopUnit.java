package io.lanu.travian.game.models;

import io.lanu.travian.enums.ENations;
import io.lanu.travian.enums.EUnits;
import io.lanu.travian.enums.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TroopUnit {
    private EUnits unitType;
    private ENations nationsType;
    private Map<Resource, BigDecimal> resources;
    private Integer offense;
    private Integer defenseInf;
    private Integer defenseCav;
    private Integer speed;
    private Integer opacity;
    private Integer eatHour;
    private long baseProductionTime; // seconds
}
