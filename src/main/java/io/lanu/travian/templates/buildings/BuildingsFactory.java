package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EBuildings;
import io.lanu.travian.enums.EResource;

import java.math.BigDecimal;
import java.util.Map;

public class BuildingsFactory {

    public static BuildingBase get(EBuildings name, int level){
        switch (name){
            case MAIN: return new MainBuilding(1, 2,
                    Map.of(EResource.WOOD, BigDecimal.valueOf(100),
                            EResource.CLAY, BigDecimal.valueOf(100),
                            EResource.IRON, BigDecimal.valueOf(100),
                            EResource.CROP, BigDecimal.valueOf(100)
                    ), null, 100);
            case EMPTY: return new EmptySpotBuilding(0);
            case WAREHOUSE: return new WarehouseBuilding(1, 0,
                    Map.of(EResource.WOOD, BigDecimal.valueOf(100),
                    EResource.CLAY, BigDecimal.valueOf(100),
                    EResource.IRON, BigDecimal.valueOf(100),
                    EResource.CROP, BigDecimal.valueOf(100)
                    ), null, 800);
        }
        return null;
    }
}
