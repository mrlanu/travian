package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.ENation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilitaryUnitResponse {
    private String id;
    private ENation nation;
    private boolean dynamic;
    private String originVillageId;
    private String originVillageName;
    private int[] originVillageCoordinates;
    private String currentLocationVillageId;
    private List<Integer> units;
    private LocalDateTime arrivalTime;
    private int expensesPerHour;
}
