package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuildingType;
import io.lanu.travian.enums.EBuildings;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.models.responses.NewBuilding;
import io.lanu.travian.templates.Time;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BuildingsFactory {

    private static final Map<EBuildings, BuildTemplate> buildings = Map.ofEntries(// should be changed to 1420
            Map.entry(EBuildings.CROPLAND, BuildTemplate.builder()
                    .type(EBuildingType.RESOURCE)
                    .resource(EResource.CROP)
                    .cost(Arrays.asList(70, 90, 70, 20))
                    .k(1.67).cu(0).cp(1).time(new Time(1450/3,1.6, 1000/3)).maxLevel(22)
                    .description("Maximum level is 10, except capital — limited by stockyards there.")
                    .isMulti(true)
                    .build()),
            Map.entry(EBuildings.CLAY_PIT, BuildTemplate.builder()
                    .type(EBuildingType.RESOURCE)
                    .resource(EResource.CLAY)
                    .cost(Arrays.asList(80, 40, 80, 50))
                    .k(1.67).cu(2).cp(1).time(new Time(1660/3,1.6, 1000/3)).maxLevel(22)
                    .description("Maximum level is 10, except capital — limited by stockyards there.")
                    .isMulti(true)
                    .build()),
            Map.entry(EBuildings.WOODCUTTER, BuildTemplate.builder()
                    .type(EBuildingType.RESOURCE)
                    .resource(EResource.WOOD)
                    .cost(Arrays.asList(40, 100, 50, 60))
                    .k(1.67).cu(2).cp(1).time(new Time(1780/3,1.6, 1000/3)).maxLevel(22)
                    .description("Maximum level is 10, except capital — limited by stockyards there.")
                    .isMulti(true)
                    .build()),
            Map.entry(EBuildings.IRON_MINE, BuildTemplate.builder()
                    .type(EBuildingType.RESOURCE)
                    .resource(EResource.IRON)
                    .cost(Arrays.asList(100, 80, 30, 60))
                    .k(1.67).cu(2).cp(1).time(new Time(2350/3,1.6, 1000/3)).maxLevel(22)
                    .description("Maximum level is 10, except capital — limited by stockyards there.")
                    .isMulti(true)
                    .build()),

            Map.entry(EBuildings.MAIN, BuildTemplate.builder()
                    .type(EBuildingType.INFRASTRUCTURE)
                    .cost(Arrays.asList(70, 40, 60, 20))
                    .k(1.28).cu(2).cp(2).time(new Time(3875)).maxLevel(20)
                    .description("Affects construction speed of other buildings. Building speed is 5x slower on 0th level (destroyed) " +
                            "comparing to 1st level.")
                    .requirementBuildings(new ArrayList<>())
                    .build()),
            Map.entry(EBuildings.WAREHOUSE, BuildTemplate.builder()
                    .type(EBuildingType.INFRASTRUCTURE)
                    .cost(Arrays.asList(130, 160, 90, 40))
                    .k(1.28).cu(1).cp(1).time(new Time(3875)).maxLevel(20)
                    .description("Limits maximum amount of resources available in village. When no it, capacity is 800.")
                    .requirementBuildings(List.of(new RequirementBuilding(EBuildings.MAIN.getName(), 1, false)))
                    .isMulti(true)
                    .build()),
            Map.entry(EBuildings.GRANARY, BuildTemplate.builder()
                    .type(EBuildingType.INFRASTRUCTURE)
                    .cost(Arrays.asList(80, 100, 70, 20))
                    .k(1.28).cu(1).cp(1).time(new Time(3475)).maxLevel(20)
                    .description("Limits maximum amount of crop available in village. When no it, capacity is 800.")
                    .requirementBuildings(List.of(new RequirementBuilding(EBuildings.MAIN.getName(), 1, false)))
                    .isMulti(true)
                    .build()),
            Map.entry(EBuildings.BARRACK, BuildTemplate.builder()
                    .type(EBuildingType.MILITARY)
                    .cost(Arrays.asList(210, 140, 260, 120))
                    .k(1.28).cu(4).cp(1).time(new Time(3875)).maxLevel(20)
                    .description("Once troops queued for training, their training time won't be changed disregarding of further changes " +
                            "in barracks level, effect from artifacts or items(T4). Even if barracks would be demolished, troops training " +
                            "will be continued.")
                    .requirementBuildings(
                            Arrays.asList(
                                    new RequirementBuilding(EBuildings.MAIN.getName(), 3, false),
                                    new RequirementBuilding(EBuildings.WAREHOUSE.getName(), 1, false),
                                    new RequirementBuilding(EBuildings.GRANARY.getName(), 1, false)))
                    .build()),
            Map.entry(EBuildings.MARKETPLACE, BuildTemplate.builder()
                    .type(EBuildingType.INFRASTRUCTURE)
                    .cost(Arrays.asList(80,  70, 120,  70))
                    .k(1.28).cu(4).cp(3).time(new Time(3675)).maxLevel(20)
                    .description("Could be used as extra stockyard (but not a cranny!), by placing sell offer. " +
                        "Keep in mind, that resources could be stolen in raid.")
                    .requirementBuildings(Arrays.asList(
                        new RequirementBuilding(EBuildings.MAIN.getName(), 1, false),
                        new RequirementBuilding(EBuildings.WAREHOUSE.getName(), 1, false),
                        new RequirementBuilding(EBuildings.GRANARY.getName(), 1, false)))
                    .build())
    );

    private static final Integer[] productions = {3, 7, 13, 21, 31, 46, 70, 98, 140, 203, 280};

    public static List<NewBuilding> getListOfNewBuildings(){
        var result = new ArrayList<NewBuilding>();
        buildings.forEach((k,v) -> {
            if (!v.getType().equals(EBuildingType.RESOURCE)){
                var template = getBuilding(k, 0);
                result.add(new NewBuilding(k.getName(), k, v.getType(), v.getDescription(), template.getResourcesToNextLevel(),
                        template.getTimeToNextLevel(), v.getRequirementBuildings(), v.getMaxLevel(), false, v.isMulti()));
            }
        });
        return result;
    }

    public static BuildingBase getBuilding(EBuildings kind, int level){
        BuildingBase result;
        var template = buildings.get(kind);
        switch (kind){
            case CROPLAND: result = new ResourceField(EResource.CROP, BigDecimal.valueOf(productions[level]));
                break;
            case CLAY_PIT: result = new ResourceField(EResource.CLAY, BigDecimal.valueOf(productions[level]));
                break;
            case WOODCUTTER: result = new ResourceField(EResource.WOOD, BigDecimal.valueOf(productions[level]));
                break;
            case IRON_MINE: result = new ResourceField(EResource.IRON, BigDecimal.valueOf(productions[level]));
                break;
            case EMPTY: result = new EmptySpotBuilding();
                break;
            case MAIN: result = new MainBuilding(100);
                break;
            case WAREHOUSE: result = new WarehouseBuilding(750);
                break;
            case GRANARY: result = new GranaryBuilding(750);
                break;
            case BARRACK: result = new BarrackBuilding(0);
                break;
            case MARKETPLACE: result = new Marketplace(1);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + kind);
        }

        if (template != null) {
            result.setLevel(level);
            result.setMaxLevel(template.getMaxLevel());
            result.setDescription(template.getDescription());
            result.setTimeToNextLevel(template.getTime().valueOf(level + 1));
            result.setResourcesToNextLevel(getResourcesToNextLevel(level, template));
            result.setRequirementBuildings(new ArrayList<>());
        }
        return result;
    }

    private static Map<EResource, BigDecimal> getResourcesToNextLevel(int level, BuildTemplate template) {
        return Map.of(
                EResource.WOOD, BigDecimal.valueOf(round(Math.pow(template.getK(), level) * template.getCost().get(0), 5)),
                EResource.CLAY, BigDecimal.valueOf(round(Math.pow(template.getK(), level) * template.getCost().get(1), 5)),
                EResource.IRON, BigDecimal.valueOf(round(Math.pow(template.getK(), level) * template.getCost().get(2), 5)),
                EResource.CROP, BigDecimal.valueOf(round(Math.pow(template.getK(), level) * template.getCost().get(3), 5))
        );
    }

    private static long round(double v, double n){
        return (long) (Math.round(v / n) * n);
    }

}
