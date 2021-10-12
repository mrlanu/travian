package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuildings;
import io.lanu.travian.enums.EResource;
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
                        Map<EResource, BigDecimal> resourcesToNextLevel,
                        List<RequirementBuilding> requirementBuildings,
                        Integer buildSpeed) {
        super(EBuildings.MAIN, level, position, resourcesToNextLevel, requirementBuildings);
        this.buildSpeed = buildSpeed;
    }

}
