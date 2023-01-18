package io.lanu.travian.game.models.battle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BattleResult {
    private double offLoses;
    private double defLosses;
    private List<Integer> buildings;
    private int wall;
}
