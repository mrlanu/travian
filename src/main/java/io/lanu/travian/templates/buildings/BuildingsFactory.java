package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuildings;
import io.lanu.travian.enums.EResource;

import java.math.BigDecimal;
import java.util.ArrayList;

public class BuildingsFactory {

    public static BuildingBase getBuilding(EBuildings buildingKind, int level){
        BuildingBase result;
        switch (buildingKind){
            case CROPLAND: result = new ResourceField(EResource.CROP, BigDecimal.valueOf(buildingKind.getProduction(level)));
                break;
            case CLAY_PIT: result = new ResourceField(EResource.CLAY, BigDecimal.valueOf(buildingKind.getProduction(level)));
                break;
            case WOODCUTTER: result = new ResourceField(EResource.WOOD, BigDecimal.valueOf(buildingKind.getProduction(level)));
                break;
            case IRON_MINE: result = new ResourceField(EResource.IRON, BigDecimal.valueOf(buildingKind.getProduction(level)));
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
                throw new IllegalStateException("Unexpected value: " + buildingKind);
        }

        if (!result.getName().equals("empty-spot")){
            result.setLevel(level);
            result.setMaxLevel(buildingKind.getMaxLevel());
            result.setDescription(buildingKind.getDescription());
            result.setTimeToNextLevel(buildingKind.getTime().valueOf(level + 1));
            result.setResourcesToNextLevel(buildingKind.getResourcesToNextLevel(level));
            result.setRequirementBuildings(new ArrayList<>());
        }


        return result;
    }
}
