package io.lanu.travian.game.entities;

import io.lanu.travian.enums.ENation;
import io.lanu.travian.enums.EResource;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
public class ReportPlayerEntity {
    private String settlementId;
    private ENation nation;
    private int[] troops;
    private int[] dead;
    private Map<EResource, BigDecimal> bounty;
    private int carry;
}
