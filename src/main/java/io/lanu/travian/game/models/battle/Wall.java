package io.lanu.travian.game.models.battle;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Wall {
    private int level;
    private int durability;

    public BattleSides<Double> getBonus(){
        return BattleSides.off(0.0, 0.0);
    }
}
