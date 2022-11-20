package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.ECombatUnitMission;
import io.lanu.travian.enums.ENation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilitaryUnitContract {
    private ENation nation;
    private ECombatUnitMission mission;
    private String originVillageId;
    private String originVillageName;
    private String originPlayerName;
    private int[] originVillageCoordinates;
    private String targetVillageId;
    private String targetVillageName;
    private String targetPlayerName;
    private int[] targetVillageCoordinates;
    private int[] units;
    private LocalDateTime arrivalTime;
    private int duration;
}
