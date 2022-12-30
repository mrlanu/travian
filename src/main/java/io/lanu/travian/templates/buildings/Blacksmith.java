package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuilding;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Blacksmith extends BuildingBase{
    public Blacksmith() {
        super();
        this.name = EBuilding.BLACKSMITH.getName();
    }
}
