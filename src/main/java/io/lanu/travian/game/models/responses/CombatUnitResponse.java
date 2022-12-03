package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.EResource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombatUnitResponse {
    private String name;
    private int level;
    private int attack;
    private int defInfantry;
    private int defCavalry;
    private int speed;
    private int capacity;
    private Map<EResource, Integer> cost;
    private int eat;
    private long time;
    private String description;

}
