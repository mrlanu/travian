package io.lanu.travian.game.models.requests;

import io.lanu.travian.enums.BuildingType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BuildingRequest {
    private BuildingType buildingType;
    private Integer level;
}
