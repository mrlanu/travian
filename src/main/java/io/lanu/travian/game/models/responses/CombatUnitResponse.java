package io.lanu.travian.game.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatUnitResponse {
    private String name;
    private int level;
    private int attack;
    private int defInfantry;
    private int defCavalry;
    private int speed;
    private int capacity;
    private List<Integer> cost;
    private int eat;
    private long time;
    private String description;

}
