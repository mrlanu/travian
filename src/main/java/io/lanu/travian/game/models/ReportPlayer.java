package io.lanu.travian.game.models;

import io.lanu.travian.enums.ENation;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class ReportPlayer {
    private String settlementId;
    private String settlementName;
    private String accountId;
    private String playerName;
    private ENation nation;
    private int[] troops;
    private int[] dead;
    private List<BigDecimal> bounty;
    private int carry;
}
