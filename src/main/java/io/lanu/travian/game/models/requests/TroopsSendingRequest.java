package io.lanu.travian.game.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TroopsSendingRequest {

    private String villageId;
    private int x;
    private int y;
    private int attackKind;
    private List<AttackWaveRequest> waves;
}
