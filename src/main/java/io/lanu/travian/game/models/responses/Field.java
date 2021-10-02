package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Field extends FieldView{
    private Resource resource;
    private BigDecimal production;
    private Map<Resource, BigDecimal> resourcesToNextLevel;
    private long timeToNextLevel;
}
