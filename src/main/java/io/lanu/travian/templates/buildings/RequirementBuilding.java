package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.BuildingType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequirementBuilding {
    private BuildingType buildingType;
    private int level;
}
