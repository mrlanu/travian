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
@Data
@NoArgsConstructor
public class WarehouseBuilding extends BuildingBase{
    private Integer capacity;

    public WarehouseBuilding(int level, int position,
                             Map<EResource, BigDecimal> resourcesToNextLevel,
                             List<RequirementBuilding> requirementBuildings,
                             Integer capacity) {
        super(EBuildings.WAREHOUSE, level, position, resourcesToNextLevel, requirementBuildings);
        this.capacity = capacity;
    }
}
