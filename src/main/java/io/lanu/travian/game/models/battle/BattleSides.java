package io.lanu.travian.game.models.battle;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BattleSides<T> {
    private T off;
    private T def;

    public static <T> BattleSides<T> off(T left, T right){
        return new BattleSides<>(left, right);
    }
}
