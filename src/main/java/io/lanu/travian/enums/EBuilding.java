package io.lanu.travian.enums;

import io.lanu.travian.templates.Time;
import io.lanu.travian.templates.buildings.RequirementBuilding;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
public enum EBuilding {
    WOODCUTTER("Woodcutter", EBuildingType.RESOURCE, EResource.WOOD, Arrays.asList(40, 100, 50, 60), 1.67, 2, 1,
            new Time(1780/3,1.6, 1000/3), 22,
            "Maximum level is 10, except capital — limited by stockyards there.",
            new ArrayList<>(), true),
    CLAY_PIT("Clay-pit", EBuildingType.RESOURCE, EResource.CROP, Arrays.asList(80, 40, 80, 50), 1.67, 2, 1,
            new Time(1660/3,1.6, 1000/3), 22,
            "Maximum level is 10, except capital — limited by stockyards there.",
            new ArrayList<>(), true),
    IRON_MINE("Iron-mine", EBuildingType.RESOURCE, EResource.CROP, Arrays.asList(100, 80, 30, 60), 1.67, 2, 1,
            new Time(2350/3,1.6, 1000/3), 22,
            "Maximum level is 10, except capital — limited by stockyards there.",
            new ArrayList<>(), true),
    CROPLAND("Cropland", EBuildingType.RESOURCE, EResource.CROP, Arrays.asList(70, 90, 70, 20), 1.67, 0, 1,
            new Time(1450/3,1.6, 1000/3), 22,
            "Maximum level is 10, except capital — limited by stockyards there.",
            new ArrayList<>(), true),

    RALLY_POINT("Rally-point", EBuildingType.MILITARY, null, Arrays.asList(110, 160, 90, 70), 1.28, 1, 1,
        new Time(3875), 20,
        "Rally point level N detects types of units in incoming attacks, unless there're more N units in that attack.",
        new ArrayList<>(), false),
    MAIN("Main-building", EBuildingType.INFRASTRUCTURE, null, Arrays.asList(70, 40, 60, 20),1.28, 2, 2,
            new Time(3875), 20,
            "Affects construction speed of other buildings. Building speed is 5x slower on 0th level (destroyed) " +
                    "comparing to 1st level.",
            new ArrayList<>(), false),

    GRANARY("Granary", EBuildingType.INFRASTRUCTURE,null, Arrays.asList(80, 100, 70, 20), 1.28, 1, 1,
            new Time(3475), 20, "Limits maximum amount of crop available in village. When no it, capacity is 800.",
            List.of(new RequirementBuilding(EBuilding.MAIN.getName(), 1, false)), true),

    WAREHOUSE("Warehouse", EBuildingType.INFRASTRUCTURE,null, Arrays.asList(130, 160, 90, 40), 1.28, 1, 1,
            new Time(3875), 20,
            "Limits maximum amount of resources available in village. When no it, capacity is 800.",
            List.of(new RequirementBuilding(EBuilding.MAIN.getName(), 1, false)), true),

    BARRACK("Barrack", EBuildingType.MILITARY,null, Arrays.asList(210, 140, 260, 120), 1.28, 4, 1,
            new Time(3875), 20,
            "Once troops queued for training, their training time won't be changed disregarding of further changes " +
                    "in barracks level, effect from artifacts or items(T4). Even if barracks would be demolished, troops training " +
                    "will be continued.",
            Arrays.asList(
                    new RequirementBuilding(EBuilding.MAIN.getName(), 3, false),
                    new RequirementBuilding(EBuilding.WAREHOUSE.getName(), 1, false),
                    new RequirementBuilding(EBuilding.GRANARY.getName(), 1, false)), false),

    MARKETPLACE("Marketplace", EBuildingType.INFRASTRUCTURE,null, Arrays.asList(80,  70, 120,  70), 1.28, 4, 3,
            new Time(3675), 20,
            "Could be used as extra stockyard (but not a cranny!), by placing sell offer. " +
                    "Keep in mind, that resources could be stolen in raid.",
            Arrays.asList(
                    new RequirementBuilding(EBuilding.MAIN.getName(), 1, false),
                    new RequirementBuilding(EBuilding.WAREHOUSE.getName(), 1, false),
                    new RequirementBuilding(EBuilding.GRANARY.getName(), 1, false)), false),

    ACADEMY("Academy", EBuildingType.MILITARY, null, Arrays.asList(220, 160,  90,  40), 1.28, 4, 4,
            new Time(3875), 20,
            "New types of troops are researched here.",
            Arrays.asList(
                    new RequirementBuilding(EBuilding.BARRACK.getName(), 3, false),
                    new RequirementBuilding(EBuilding.MAIN.getName(), 3, false)), false),

    BLACKSMITH("Smithy", EBuildingType.MILITARY, null, Arrays.asList(170,  200, 380,  130), 1.28, 4, 2,
            new Time(3875), 20,
            "Troops' weapons are enhanced here.",
            Arrays.asList(
                    new RequirementBuilding(EBuilding.MAIN.getName(), 3, false),
                    new RequirementBuilding(EBuilding.ACADEMY.getName(), 1, false)), false),

    EMPTY("empty-spot", EBuildingType.EMPTY);

    private static final Integer[] productions = {3, 7, 13, 21, 31, 46, 70, 98, 140, 203, 280};
    private static final Integer[] capacity = {800, 1200, 1700, 2300, 3100, 4000, 5000, 6300, 7700, 9600, 12000,
            14400, 18000, 22000, 26000, 32000, 38000, 45000, 55000, 66000, 80000};

    private final String name;
    private final EBuildingType type;
    private final EResource resource;
    private final List<Integer> cost;
    private final double k;
    private final int cu;
    private final int cp;
    private final Time time;
    private final int maxLevel;
    private final String description;
    private final List<RequirementBuilding> requirementBuildings;
    private final boolean isMulti;

    EBuilding(String name, EBuildingType type){
        this.name = name;
        this.resource = null;
        this.cost = null;
        this.type = EBuildingType.EMPTY;
        this.k = 0;
        this.cu = 0;
        this.cp = 0;
        this.time = null;
        this.maxLevel = 0;
        this.description = null;
        this.requirementBuildings = null;
        this.isMulti = true;
    }

    EBuilding(String name, EBuildingType type, EResource resource, List<Integer> cost, double k, int cu, int cp,
              Time time, int maxLevel, String description, List<RequirementBuilding> requirementBuildings, boolean isMulti) {
        this.name = name;
        this.resource = resource;
        this.cost = cost;
        this.type = type;
        this.k = k;
        this.cu = cu;
        this.cp = cp;
        this.time = time;
        this.maxLevel = maxLevel;
        this.description = description;
        this.requirementBuildings = requirementBuildings;
        this.isMulti = isMulti;
    }

    public Map<EResource, BigDecimal> getResourcesToNextLevel(int level) {
        return Map.of(
                EResource.WOOD, BigDecimal.valueOf(round(Math.pow(k, level) * cost.get(0), 5)),
                EResource.CLAY, BigDecimal.valueOf(round(Math.pow(k, level) * cost.get(1), 5)),
                EResource.IRON, BigDecimal.valueOf(round(Math.pow(k, level) * cost.get(2), 5)),
                EResource.CROP, BigDecimal.valueOf(round(Math.pow(k, level) * cost.get(3), 5))
        );
    }

    public int getProduction(int level){
        return productions[level];
    }

    public int getCapacity(int level) { return capacity[level]; }

    private long round(double v, double n){
        return (long) (Math.round(v / n) * n);
    }

}
