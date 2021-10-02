package io.lanu.travian.game.models.requests;

import io.lanu.travian.enums.EBuildings;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BuildingRequest {
    private EBuildings buildingType;
    private Integer level;
}
