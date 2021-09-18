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
@NoArgsConstructor
@Data
public class MainBuilding extends BuildingBase {
    private Integer buildSpeed;

    public MainBuilding(int level, int position,
                        Map<Resource, BigDecimal> resourcesToNextLevel,
                        List<RequirementBuilding> requirementBuildings,
                        Integer buildSpeed) {
        super(BuildingType.MAIN, level, position, resourcesToNextLevel, requirementBuildings);
        this.buildSpeed = buildSpeed;
    }

}
