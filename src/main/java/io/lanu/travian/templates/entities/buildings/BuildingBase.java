package io.lanu.travian.templates.entities.buildings;

import io.lanu.travian.enums.BuildingType;
import io.lanu.travian.enums.Resource;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Map;

@NoArgsConstructor
@Data
@Document("buildings")
public class BuildingBase {

    protected BuildingType buildingType;
    protected int level;
    protected int position;
    protected Map<Resource, BigDecimal> resourcesToNextLevel;

    public BuildingBase(BuildingType buildingType,
                        int level, int position,
                        Map<Resource, BigDecimal> resourcesToNextLevel) {
        this.buildingType = buildingType;
        this.level = level;
        this.position = position;
        this.resourcesToNextLevel = resourcesToNextLevel;
    }
}
