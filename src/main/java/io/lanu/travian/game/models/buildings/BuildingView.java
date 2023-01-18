package io.lanu.travian.game.models.buildings;

import io.lanu.travian.game.entities.events.ConstructionEventEntity;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

@Data
@Builder
public class BuildingView {

    protected String name;
    protected int level;
    protected int position;
    protected boolean underUpgrade;
    protected boolean ableToUpgrade;
    protected int maxLevel;
    protected String description;
    protected long timeToNextLevel;
    protected List<BigDecimal> resourcesToNextLevel;
    protected List<RequirementBuilding> requirementBuildings;

    public void setAbleToUpgrade(List<BigDecimal> storage) {
        this.ableToUpgrade = IntStream.range(0, 4)
                .noneMatch(i -> storage.get(i).compareTo(resourcesToNextLevel.get(i)) < 0);
    }

    public void setUnderUpgrade(List<ConstructionEventEntity> eventList) {
        this.underUpgrade =
                eventList.stream().anyMatch(e -> this.position == e.getBuildingPosition());
    }
}
