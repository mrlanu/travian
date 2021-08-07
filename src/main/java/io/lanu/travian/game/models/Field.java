package io.lanu.travian.game.models;

import io.lanu.travian.enums.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Field {
    private int position;
    private Resource fieldType;
    private int level;
    private BigDecimal production;
    private Map<Resource, BigDecimal> resourcesToNextLevel;
    private boolean underUpgrade;
    private boolean ableToUpgrade;
}
