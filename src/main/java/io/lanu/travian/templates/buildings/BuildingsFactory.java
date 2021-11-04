package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuildings;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.models.responses.FieldView;
import io.lanu.travian.templates.Time;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

public class BuildingsFactory {

    private static final Map<EBuildings, BuildTemplate> buildings = Map.of(
            EBuildings.CROPLAND, new BuildTemplate(EResource.CROP, Arrays.asList(70, 90, 70, 20),
                    1.67, 0, 1, new Time(1450/3,1.6, 1000/3), 10, "Crop field description"), // should be changed to 1420
            EBuildings.CLAY_PIT, new BuildTemplate(EResource.CLAY, Arrays.asList(80, 40, 80, 50),
                    1.67, 2, 1, new Time(1660/3,1.6, 1000/3), 10, "Clay field description"),
            EBuildings.WOODCUTTER, new BuildTemplate(EResource.WOOD, Arrays.asList(40, 100, 50, 60),
                    1.67, 2, 1, new Time(1780/3,1.6, 1000/3), 10, "Wood field description"),
            EBuildings.IRON_MINE, new BuildTemplate(EResource.IRON, Arrays.asList(100, 80, 30, 60),
                    1.67, 2, 1, new Time(2350/3,1.6, 1000/3), 10, "Iron field description"),
            EBuildings.MAIN, new BuildTemplate(null, Arrays.asList(70, 40, 60, 20),
                    1.28, 2, 2, new Time(3875), 20, "Affects construction speed of other buildings. Building speed is 5x slower on 0th level (destroyed) comparing to 1st level."), // should be changed to 1420
            EBuildings.WAREHOUSE, new BuildTemplate(null, Arrays.asList(130, 160, 90, 40),
                    1.28, 1, 1, new Time(3875), 20, "Limits maximum amount of resources available in village. When no it, capacity is 800."),
            EBuildings.GRANARY, new BuildTemplate(null, Arrays.asList(80, 100, 70, 20),
                    1.28, 1, 1, new Time(3475), 20, "Limits maximum amount of crop available in village. When no it, capacity is 800."),
            EBuildings.BARRACK, new BuildTemplate(EResource.IRON, Arrays.asList(210, 140, 260, 120),
                    1.28, 4, 1, new Time(3875), 20, "Once troops queued for training, their training time won't be changed disregarding of further changes in barracks level, effect from artifacts or items(T4). Even if barracks would be demolished, troops training will be continued.")
    );

    private static final Integer[] productions = {3, 7, 13, 21, 31, 46, 70, 98, 140, 203, 280};


    public static FieldView getField(EBuildings name, int level){
        var template = buildings.get(name);
        FieldView result = new FieldView();
        result.setResource(template.getResource());
        result.setLevel(level);
        result.setMaxLevel(template.getMaxLevel());
        result.setProduction(BigDecimal.valueOf(productions[level]));
        result.setName(name.getName());
        result.setDescription(template.getDescription());
        result.setResourcesToNextLevel(getResourcesToNextLevel(level, template));
        result.setTimeToNextLevel(template.getTime().valueOf(level + 1));
        return result;
    }

    public static BuildingBase getBuilding(EBuildings name, int level){
        var template = buildings.get(name);
        Map<EResource, BigDecimal> resToNextLevel = (template != null)? getResourcesToNextLevel(level, template) : null;
        switch (name){
            case MAIN: return new MainBuilding(level, 0, resToNextLevel, null, 100);
            case EMPTY: return new EmptySpotBuilding(0);
            case WAREHOUSE: return new WarehouseBuilding(level, 0, resToNextLevel, null, 800);
            case GRANARY: return new GranaryBuilding(level, 0, resToNextLevel, null, 800);
        }
        return null;
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
