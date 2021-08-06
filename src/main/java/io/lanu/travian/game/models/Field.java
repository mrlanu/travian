package io.lanu.travian.game.models;

import io.lanu.travian.enums.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Field {
    private int position;
    private Resource fieldType;
    private int level;
    private int production;
    private Map<Resource, Integer> resourcesToNextLevel;
    private boolean underUpgrade;
    private boolean ableToUpgrade;
}
