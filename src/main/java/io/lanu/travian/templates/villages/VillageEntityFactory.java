package io.lanu.travian.templates.villages;

import io.lanu.travian.enums.EBuildings;
import io.lanu.travian.enums.Resource;
import io.lanu.travian.enums.VillageType;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.BuildModel;

import java.math.BigDecimal;
import java.util.Map;

public class VillageEntityFactory {

    public static VillageEntity getVillageByType(VillageType villageType){
        switch (villageType){
            case SIX: return new VillageEntity(null, null, "New Village", 100, 100,
                    VillageType.SIX, 100, 100,
                    Map.of(
                            1, new BuildModel(EBuildings.CROPLAND, 1),
                            2, new BuildModel(EBuildings.WOODCUTTER, 1),
                            3, new BuildModel(EBuildings.CROPLAND, 0),
                            4, new BuildModel(EBuildings.CLAY_PIT, 1),
                            5, new BuildModel(EBuildings.IRON_MINE, 0),

                            6, new BuildModel(EBuildings.MAIN, 1),
                            7, new BuildModel(EBuildings.BARRACK, 1)),
                    Map.of(Resource.CROP, BigDecimal.valueOf(500),
                            Resource.CLAY, BigDecimal.valueOf(500),
                            Resource.WOOD, BigDecimal.valueOf(500),
                            Resource.IRON, BigDecimal.valueOf(500)),
                    null);
            default: return null;
        }
    }

}
