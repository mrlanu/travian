package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.ENation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TileDetail {
    private String id;
    private ENation nation;
    private String playerName;
    private String name;
    private int x;
    private int y;
    private int population;
    private double distance;
    private boolean village;
}
