package io.lanu.travian.game.models.requests;

import io.lanu.travian.enums.ECombatGroupMission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombatGroupSendingRequest {
    private String targetSettlementId;
    private ECombatGroupMission mission;
    private List<AttackWaveRequest> waves;
}
