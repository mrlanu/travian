package io.lanu.travian.game.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VillageBrief {
    private String villageId;
    private String villageName;
    private String playerName;
    private int[] coordinates;
}
