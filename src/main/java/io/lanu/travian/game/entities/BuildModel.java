package io.lanu.travian.game.entities;

import io.lanu.travian.enums.EBuildings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BuildModel {
    private EBuildings kind;
    private int level;
}
