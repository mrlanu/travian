package io.lanu.travian.templates.villages;

import io.lanu.travian.enums.EBuildings;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.enums.EVillageType;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.BuildModel;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class VillageEntityFactory {

    public static VillageEntity getVillageByType(EVillageType villageType){
        switch (villageType){
            case SIX: return new VillageEntity(null, null, "New Village", 0, 0,
                    EVillageType.SIX, 100, 100, 100,
                    Map.ofEntries(
                            Map.entry(1, new BuildModel(EBuildings.WOODCUTTER, 1)),
                            Map.entry(2, new BuildModel(EBuildings.CROPLAND, 1)),
                            Map.entry(3, new BuildModel(EBuildings.WOODCUTTER, 0)),
                            Map.entry(4, new BuildModel(EBuildings.IRON_MINE, 0)),
                            Map.entry(5, new BuildModel(EBuildings.CLAY_PIT, 1)),
                            Map.entry(6, new BuildModel(EBuildings.CLAY_PIT, 1)),
                            Map.entry(7, new BuildModel(EBuildings.IRON_MINE, 1)),
                            Map.entry(8, new BuildModel(EBuildings.CROPLAND, 0)),
                            Map.entry(9, new BuildModel(EBuildings.CROPLAND, 0)),
                            Map.entry(10, new BuildModel(EBuildings.IRON_MINE, 1)),
                            Map.entry(11, new BuildModel(EBuildings.IRON_MINE, 1)),
                            Map.entry(12, new BuildModel(EBuildings.CROPLAND, 1)),
                            Map.entry(13, new BuildModel(EBuildings.CROPLAND, 0)),
                            Map.entry(14, new BuildModel(EBuildings.CROPLAND, 0)),
                            Map.entry(15, new BuildModel(EBuildings.CLAY_PIT, 1)),
                            Map.entry(16, new BuildModel(EBuildings.WOODCUTTER, 0)),
                            Map.entry(17, new BuildModel(EBuildings.CLAY_PIT, 0)),
                            Map.entry(18, new BuildModel(EBuildings.WOODCUTTER, 1)),

                            Map.entry(19, new BuildModel(EBuildings.EMPTY, 0)),
                            Map.entry(20, new BuildModel(EBuildings.EMPTY, 0)),
                            Map.entry(21, new BuildModel(EBuildings.EMPTY, 0)),
                            Map.entry(22, new BuildModel(EBuildings.EMPTY, 0)),
                            Map.entry(23, new BuildModel(EBuildings.EMPTY, 0)),
                            Map.entry(24, new BuildModel(EBuildings.BARRACK, 1)),
                            Map.entry(25, new BuildModel(EBuildings.EMPTY, 0)),
                            Map.entry(26, new BuildModel(EBuildings.MAIN, 1)),
                            Map.entry(27, new BuildModel(EBuildings.EMPTY, 0)),
                            Map.entry(28, new BuildModel(EBuildings.EMPTY, 0)),
                            Map.entry(29, new BuildModel(EBuildings.EMPTY, 0)),
                            Map.entry(30, new BuildModel(EBuildings.GRANARY, 1)),
                            Map.entry(31, new BuildModel(EBuildings.EMPTY, 0)),
                            Map.entry(32, new BuildModel(EBuildings.EMPTY, 0)),
                            Map.entry(33, new BuildModel(EBuildings.EMPTY, 0)),
                            Map.entry(34, new BuildModel(EBuildings.WAREHOUSE, 20)),
                            Map.entry(35, new BuildModel(EBuildings.EMPTY, 0)),
                            Map.entry(36, new BuildModel(EBuildings.EMPTY, 0)),
                            Map.entry(37, new BuildModel(EBuildings.EMPTY, 0)),
                            Map.entry(38, new BuildModel(EBuildings.EMPTY, 0)),
                            Map.entry(39, new BuildModel(EBuildings.EMPTY, 0))),
                    Map.of(EResource.CROP, BigDecimal.valueOf(500),
                            EResource.CLAY, BigDecimal.valueOf(500),
                            EResource.WOOD, BigDecimal.valueOf(500),
                            EResource.IRON, BigDecimal.valueOf(500)),
                    new HashMap<>(),
                    null);
            default: return null;
        }
    }

}
