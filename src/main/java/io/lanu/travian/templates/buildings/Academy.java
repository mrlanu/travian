package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuilding;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Academy extends BuildingBase{
    public Academy() {
        super();
        this.name = EBuilding.ACADEMY.getName();
    }
}
