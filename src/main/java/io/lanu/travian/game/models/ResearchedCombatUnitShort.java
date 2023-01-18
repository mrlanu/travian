package io.lanu.travian.game.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResearchedCombatUnitShort {
    private int unit; // number in UNITS array
    private int level;
}
