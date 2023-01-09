package io.lanu.travian.game.entities;

import io.lanu.travian.enums.ENation;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class ReportPlayerEntity {
    private String settlementId;
    private ENation nation;
    private int[] troops;
    private int[] dead;
    private List<BigDecimal> bounty;
    private int carry;
}
