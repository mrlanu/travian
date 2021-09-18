package io.lanu.travian.game.entities;

import io.lanu.travian.enums.BuildingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BuildingEntity {
    private BuildingType buildingType;
    private int level;
}
