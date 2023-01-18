package io.lanu.travian.game.entities;

import io.lanu.travian.enums.ENation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ReportPlayerEntity {
    private String settlementId;
    private ENation nation;
    private int[] troops;
    private int[] dead;
    @Builder.Default
    private List<BigDecimal> bounty = Arrays
            .asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    private int carry;
}
