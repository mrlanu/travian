package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuildings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class WarehouseBuilding extends BuildingBase{
    private BigDecimal capacity;

    public WarehouseBuilding(BigDecimal capacity) {
        super();
        this.name = EBuildings.WAREHOUSE.getName();
        this.capacity = capacity;
    }
}
