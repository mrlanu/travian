package io.lanu.travian.game.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VillageBrief {
    private String villageId;
    private String villageName;
    private String playerName;
    private int[] coordinates;

    public VillageBrief(String villageName, String playerName, int[] coordinates) {
        this.villageName = villageName;
        this.playerName = playerName;
        this.coordinates = coordinates;
    }
}
