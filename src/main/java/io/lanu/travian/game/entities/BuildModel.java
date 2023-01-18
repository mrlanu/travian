package io.lanu.travian.game.entities;

import io.lanu.travian.game.models.buildings.BuildingsID;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BuildModel {
    private BuildingsID id;
    private int level;
}
