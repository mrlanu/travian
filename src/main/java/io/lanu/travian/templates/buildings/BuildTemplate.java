package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EResource;
import io.lanu.travian.templates.Time;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BuildTemplate {
    private EResource resource;
    private List<Integer> cost;
    private double k;
    private int cu;
    private int cp;
    private Time time;
    private int maxLevel;
    private String description;
}
