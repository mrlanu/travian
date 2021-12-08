package io.lanu.travian.game.models.responses;

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
public class MilitaryUnitResponse {
    private String id;
    private ENation nation;
    private boolean move;
    private String mission;
    private String originVillageId;
    private String originVillageName;
    private int[] originVillageCoordinates;
    private String currentLocationVillageId;
    private String targetVillageId;
    private String targetVillageName;
    private String targetPlayerName;
    private int[] targetVillageCoordinates;
    private int[] units;
    private LocalDateTime arrivalTime;
    private long duration;
    private int expensesPerHour;
}
