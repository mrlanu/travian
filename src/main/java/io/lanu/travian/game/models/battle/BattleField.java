package io.lanu.travian.game.models.battle;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BattleField {
    private int tribe;
    private int population;
    private int durBonus;
    private Wall wall;
    private int def;
    private boolean party;
}
