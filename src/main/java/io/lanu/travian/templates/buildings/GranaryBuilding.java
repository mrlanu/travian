package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuildings;
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
public class GranaryBuilding extends BuildingBase {
    private Integer capacity;

    public GranaryBuilding(int level, int position,
                           Map<Resource, BigDecimal> resourcesToNextLevel,
                           List<RequirementBuilding> requirementBuildings,
                           Integer capacity) {
        super(EBuildings.GRANARY, level, position, resourcesToNextLevel, requirementBuildings);
        this.capacity = capacity;
    }

}
