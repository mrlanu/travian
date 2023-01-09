package io.lanu.travian.templates.buildings;

import io.lanu.travian.game.entities.events.ConstructionEventEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

@NoArgsConstructor
@Data
@Document("buildings")
public class BuildingBase implements IResourceProd{

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
