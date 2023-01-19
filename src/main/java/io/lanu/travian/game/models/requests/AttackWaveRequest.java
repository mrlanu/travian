package io.lanu.travian.game.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttackWaveRequest {

    private List<Integer> units;
    private int firstTarget;
    private int secondTarget;
    private String firstTargetText;
    private String secondTargetText;
}

