package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuildings;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RallyPoint extends BuildingBase {

    public RallyPoint() {
        super();
        this.name = EBuildings.RALLY_POINT.getName();
    }

}
