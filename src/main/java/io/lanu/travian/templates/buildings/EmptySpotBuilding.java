package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuilding;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmptySpotBuilding extends BuildingBase {
    public EmptySpotBuilding() {
        super();
        this.name = EBuilding.EMPTY.getName();
    }
}
