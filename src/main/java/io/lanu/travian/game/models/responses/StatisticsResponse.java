package io.lanu.travian.game.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    private int index;
    private String id;
    private String playerName;
    private String playerId;
    private int population;
    private int villagesCount;
    private String allianceName;
    private long attackPoints;
    private long defensePoints;
}
