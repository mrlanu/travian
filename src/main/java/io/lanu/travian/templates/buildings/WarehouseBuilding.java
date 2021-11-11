package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuildings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class WarehouseBuilding extends BuildingBase{
    private Integer capacity;

    public WarehouseBuilding(Integer capacity) {
        super();
        this.name = EBuildings.GRANARY.getName();
        this.capacity = capacity;
    }
}
