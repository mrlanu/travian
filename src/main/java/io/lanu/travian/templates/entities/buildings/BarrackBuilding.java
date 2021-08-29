package io.lanu.travian.templates.entities.buildings;

import io.lanu.travian.enums.BuildingType;
import io.lanu.travian.enums.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BarrackBuilding extends BuildingBase {
    private Integer timeReduction;

    public BarrackBuilding(int level, int position,
                           Map<Resource, BigDecimal> resourcesToNextLevel,
                           Integer timeReduction) {
        super(BuildingType.BARRACK, level, position, resourcesToNextLevel);
        this.timeReduction = timeReduction;
    }
}
