package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FieldView {
    private Resource fieldType;
    private int level;
    private BigDecimal production;
    private List<Long> resourcesToNextLevel;
    private long timeToNextLevel;
    private boolean underUpgrade;
    private boolean ableToUpgrade;
}
