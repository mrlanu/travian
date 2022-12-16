package io.lanu.travian.game.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsEntity {
    @Id
    private String id;
    private String playerName;
    private String playerId;
    private int population;
    private int villagesCount;
    private String allianceName;
    private long attackPoints;
    private long defensePoints;

    public StatisticsEntity(String playerName, String playerId, int population,
                            int villagesCount, String allianceName, long attackPoints,
                            long defensePoints) {
        this.playerName = playerName;
        this.playerId = playerId;
        this.population = population;
        this.villagesCount = villagesCount;
        this.allianceName = allianceName;
        this.attackPoints = attackPoints;
        this.defensePoints = defensePoints;
    }

    public void addVillage(int amount){
        this.villagesCount += amount;
    }

    public void addAttackPoints(long amount){
        this.attackPoints += amount;
    }

    public void addDefensePoints(long amount){
        this.defensePoints += amount;
    }
}
