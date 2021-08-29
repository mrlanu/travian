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
public class GranaryBuilding extends BuildingBase {
    private Integer capacity;

    public GranaryBuilding(int level, int position,
                           Map<Resource, BigDecimal> resourcesToNextLevel,
                           Integer capacity) {
        super(BuildingType.GRANARY, level, position, resourcesToNextLevel);
        this.capacity = capacity;
    }

}
