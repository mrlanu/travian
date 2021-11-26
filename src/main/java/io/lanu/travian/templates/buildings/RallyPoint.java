package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuilding;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RallyPoint extends BuildingBase {

    public RallyPoint() {
        super();
        this.name = EBuilding.RALLY_POINT.getName();
    }

}
