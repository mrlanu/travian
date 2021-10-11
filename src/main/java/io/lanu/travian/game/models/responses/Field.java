package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.Resource;
import io.lanu.travian.game.entities.events.BuildIEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
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

    @Override
    public void setAbleToUpgrade(Map<Resource, BigDecimal> storage) {
        this.ableToUpgrade = resourcesToNextLevel.entrySet().stream()
                .noneMatch(x -> storage.get(x.getKey()).compareTo(x.getValue()) < 0);

    }

    @Override
    public void setUnderUpgrade(List<BuildIEvent> eventList) {
        this.underUpgrade = eventList.stream().anyMatch(e -> this.position == e.getBuildingPosition());
    }
}
