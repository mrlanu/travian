package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.entities.events.BuildIEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FieldView {
    private int position;
    private int level;
    private String name;
    private boolean underUpgrade;
    private boolean ableToUpgrade;
    protected String description;
    private EResource resource;
    private BigDecimal production;
    private Map<EResource, BigDecimal> resourcesToNextLevel;
    private long timeToNextLevel;

    public void setAbleToUpgrade(Map<EResource, BigDecimal> storage) {
        this.ableToUpgrade = resourcesToNextLevel.entrySet().stream()
                .noneMatch(x -> storage.get(x.getKey()).compareTo(x.getValue()) < 0);
    }

    public void setUnderUpgrade(List<BuildIEvent> eventList) {
        this.underUpgrade =
                eventList.stream().anyMatch(e -> this.position == e.getBuildingPosition());
    }
}
