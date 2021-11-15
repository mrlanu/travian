package io.lanu.travian.templates.buildings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequirementBuilding {
    private String name;
    private int level;
    private boolean exist;
}
