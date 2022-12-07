package io.lanu.travian.game.models;

import io.lanu.travian.enums.EResource;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ReportPlayer {
    private String settlementId;
    private int[] troops;
    private int[] dead;
    private Map<EResource, Integer> bounty;
    private int carry;
}
