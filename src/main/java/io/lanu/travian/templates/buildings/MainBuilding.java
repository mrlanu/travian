package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuilding;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class MainBuilding extends BuildingBase {
    private Integer buildSpeed;

    public MainBuilding(Integer buildSpeed) {
        super();
        this.name = EBuilding.MAIN.getName();
        this.buildSpeed = buildSpeed;
    }

}
