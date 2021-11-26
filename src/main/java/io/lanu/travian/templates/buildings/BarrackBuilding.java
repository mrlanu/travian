package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuilding;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BarrackBuilding extends BuildingBase {
    private Integer timeReduction;

    public BarrackBuilding(Integer timeReduction) {
        super();
        this.name = EBuilding.BARRACK.getName();
        this.timeReduction = timeReduction;
    }
}
