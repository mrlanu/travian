package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuildings;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmptySpotBuilding extends BuildingBase {
    public EmptySpotBuilding() {
        super();
        this.name = EBuildings.EMPTY.getName();
    }
}
