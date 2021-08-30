package io.lanu.travian.templates.entities.buildings;

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
public class WarehouseBuilding extends BuildingBase{
    private Integer capacity;

    public WarehouseBuilding(int level, int position,
                             Map<Resource, BigDecimal> resourcesToNextLevel,
                             List<RequirementBuilding> requirementBuildings,
                             Integer capacity) {
        super(BuildingType.WAREHOUSE, level, position, resourcesToNextLevel, requirementBuildings);
        this.capacity = capacity;
    }
}
