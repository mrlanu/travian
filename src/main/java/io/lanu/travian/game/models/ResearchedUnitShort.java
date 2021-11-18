package io.lanu.travian.game.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResearchedUnitShort {
    private String name;
    private int level;
}
