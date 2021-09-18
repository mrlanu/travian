package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.BuildingType;
import io.lanu.travian.enums.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class EmptySpotBuilding extends BuildingBase {
    public EmptySpotBuilding(int level, int position,
                             Map<Resource, BigDecimal> resourcesToNextLevel,
                             List<RequirementBuilding> requirementBuildings) {
        super(BuildingType.EMPTY, level, position, resourcesToNextLevel, requirementBuildings);
    }
}
