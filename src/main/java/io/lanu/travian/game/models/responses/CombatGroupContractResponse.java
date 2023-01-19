package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.ECombatGroupMission;
import io.lanu.travian.enums.ENation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatGroupContractResponse {
    private String savedEntityId;
    private ECombatGroupMission mission;
    private String targetVillageId;
    private String targetVillageName;
    private String targetPlayerName;
    private int[] targetVillageCoordinates;
    private List<Integer> units;
    private LocalDateTime arrivalTime;
    private int duration;
}
