package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.entities.events.BuildIEvent;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
    protected Map<EResource, BigDecimal> resourcesToNextLevel;
    protected List<RequirementBuilding> requirementBuildings;

    public void setAbleToUpgrade(Map<EResource, BigDecimal> storage) {
        this.ableToUpgrade = resourcesToNextLevel.entrySet().stream()
                .noneMatch(x -> storage.get(x.getKey()).compareTo(x.getValue()) < 0);
    }

    public void setUnderUpgrade(List<BuildIEvent> eventList) {
        this.underUpgrade =
                eventList.stream().anyMatch(e -> this.position == e.getBuildingPosition());
    }
}
