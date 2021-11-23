package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuildings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class GranaryBuilding extends BuildingBase {
    private BigDecimal capacity;

    public GranaryBuilding(BigDecimal capacity) {
        super();
        this.name = EBuildings.GRANARY.getName();
        this.capacity = capacity;
    }

}
