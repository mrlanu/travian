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
            case SIX: return new VillageEntity(null, null, "New Village", 100, 100,
                    EVillageType.SIX, 100, 100,
                    Map.of(
                            1, new BuildModel(EBuildings.CROPLAND, 1),
                            2, new BuildModel(EBuildings.WOODCUTTER, 1),
                            3, new BuildModel(EBuildings.IRON_MINE, 0),
                            4, new BuildModel(EBuildings.CROPLAND, 1),
                            5, new BuildModel(EBuildings.CLAY_PIT, 0),

                            6, new BuildModel(EBuildings.MAIN, 1),
                            7, new BuildModel(EBuildings.BARRACK, 1)),
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
