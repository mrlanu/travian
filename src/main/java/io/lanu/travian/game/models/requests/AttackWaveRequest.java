package io.lanu.travian.game.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttackWaveRequest {

    private int[] troops;
    private int firstTarget;
    private int secondTarget;
    private String firstTargetText;
    private String secondTargetText;
}

