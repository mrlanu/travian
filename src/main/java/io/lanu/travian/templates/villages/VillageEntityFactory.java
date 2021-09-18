package io.lanu.travian.templates.villages;

import io.lanu.travian.enums.Resource;
import io.lanu.travian.enums.VillageType;
import io.lanu.travian.game.entities.FieldEntity;
import io.lanu.travian.game.entities.VillageEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

public class VillageEntityFactory {

    private static final Map<VillageType, VillageEntity> villages = Map.of(
            VillageType.SIX, new VillageEntity(null, null, 100, 100, VillageType.SIX, 100, 100,
                    Arrays.asList(
                            new FieldEntity(Resource.CROP, 0),
                            new FieldEntity(Resource.WOOD, 0),
                            new FieldEntity(Resource.CROP, 0),
                            new FieldEntity(Resource.CLAY, 0),
                            new FieldEntity(Resource.IRON, 0)), null,
                    Map.of(Resource.CROP, BigDecimal.valueOf(500),
                            Resource.CLAY, BigDecimal.valueOf(500),
                            Resource.WOOD, BigDecimal.valueOf(500),
                            Resource.IRON, BigDecimal.valueOf(500)),
                    null, null, null)
    );

    public static VillageEntity get(VillageType villageType){
        return villages.get(VillageType.SIX);
    }

}
