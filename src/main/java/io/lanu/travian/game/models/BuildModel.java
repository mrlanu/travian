package io.lanu.travian.game.models;

import io.lanu.travian.enums.EBuildings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BuildModel {
    private EBuildings buildingName;
    private int level;
}