package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.EBuildingType;
import io.lanu.travian.enums.EBuilding;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.entities.BuildModel;
import io.lanu.travian.templates.buildings.RequirementBuilding;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewBuilding {
    private String name;
    private EBuilding kind;
    private EBuildingType type; // infrastructure | military | industrial
    private String description;
    private Map<EResource, BigDecimal> cost;
    private long time;
    private List<RequirementBuilding> requirements;
    private int maxLevel;
    private boolean available;
    private boolean multi;

    public void checkAvailability(Collection<BuildModel> buildings, Map<EResource, BigDecimal> storage){
        requirements.forEach(requirementBuilding -> {
            var isBuildingExist = buildings.stream().anyMatch(
                    buildModel -> buildModel.getKind().getName().equals(requirementBuilding.getName())
                            && buildModel.getLevel() >= requirementBuilding.getLevel());
            if (isBuildingExist){
                requirementBuilding.setExist(true);
            }
        });
        var isAllBuildingsExist = requirements.stream().allMatch(RequirementBuilding::isExist);
        var isEnoughResources = cost.entrySet().stream()
                .noneMatch(x -> storage.get(x.getKey()).compareTo(x.getValue()) < 0);
        available = (isAllBuildingsExist && isEnoughResources);

    }

    public boolean isBuildingExistAndMaxLevelAndMulti(Map<Integer, BuildModel> buildingsMap){
        var isExist = buildingsMap.values()
                .stream()
                .anyMatch(buildModel -> buildModel.getKind().equals(this.getKind()));
        var isMaxLevel = buildingsMap.values()
                .stream()
                .anyMatch(buildModel ->
                        buildModel.getKind().equals(this.getKind()) && buildModel.getLevel() == maxLevel);
        if (isExist){
            if (isMaxLevel){
                return multi;
            }
            return false;
        }
        return true;
    }
}
