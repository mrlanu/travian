package io.lanu.travian.game.models.buildings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequirementBuilding {
    private BuildingsID id;
    private String name;
    private int level;
    private boolean exist;

    public RequirementBuilding(BuildingsID id, String name, int level) {
        this.id = id;
        this.name = name;
        this.level = level;
    }
}
