package io.lanu.travian.templates.villages;

import io.lanu.travian.enums.*;
import io.lanu.travian.game.entities.BuildModel;
import io.lanu.travian.game.entities.SettlementEntity;

import java.math.BigDecimal;
import java.util.Map;

public class VillageEntityFactory {

    public static SettlementEntity getVillageByType(EVillageType villageType){
        switch (villageType){
            case SIX: return new SettlementEntity(null, null, ENation.GALLS, "New Village", 0, 0,
                    EVillageType.SIX, 100, 100, 100,
                    Map.ofEntries(
                            Map.entry(1, new BuildModel(EBuilding.WOODCUTTER, 1)),
                            Map.entry(2, new BuildModel(EBuilding.CROPLAND, 1)),
                            Map.entry(3, new BuildModel(EBuilding.WOODCUTTER, 0)),
                            Map.entry(4, new BuildModel(EBuilding.IRON_MINE, 0)),
                            Map.entry(5, new BuildModel(EBuilding.CLAY_PIT, 1)),
                            Map.entry(6, new BuildModel(EBuilding.CLAY_PIT, 1)),
                            Map.entry(7, new BuildModel(EBuilding.IRON_MINE, 1)),
                            Map.entry(8, new BuildModel(EBuilding.CROPLAND, 0)),
                            Map.entry(9, new BuildModel(EBuilding.CROPLAND, 0)),
                            Map.entry(10, new BuildModel(EBuilding.IRON_MINE, 1)),
                            Map.entry(11, new BuildModel(EBuilding.IRON_MINE, 1)),
                            Map.entry(12, new BuildModel(EBuilding.CROPLAND, 1)),
                            Map.entry(13, new BuildModel(EBuilding.CROPLAND, 0)),
                            Map.entry(14, new BuildModel(EBuilding.CROPLAND, 0)),
                            Map.entry(15, new BuildModel(EBuilding.CLAY_PIT, 1)),
                            Map.entry(16, new BuildModel(EBuilding.WOODCUTTER, 0)),
                            Map.entry(17, new BuildModel(EBuilding.CLAY_PIT, 0)),
                            Map.entry(18, new BuildModel(EBuilding.WOODCUTTER, 1)),

                            Map.entry(19, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(20, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(21, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(22, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(23, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(24, new BuildModel(EBuilding.BARRACK, 1)),
                            Map.entry(25, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(26, new BuildModel(EBuilding.MAIN, 1)),
                            Map.entry(27, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(28, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(29, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(30, new BuildModel(EBuilding.GRANARY, 1)),
                            Map.entry(31, new BuildModel(EBuilding.RALLY_POINT, 1)),
                            Map.entry(32, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(33, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(34, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(35, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(36, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(37, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(38, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(39, new BuildModel(EBuilding.EMPTY, 0))),
                    Map.of(EResource.CROP, BigDecimal.valueOf(500),
                            EResource.CLAY, BigDecimal.valueOf(500),
                            EResource.WOOD, BigDecimal.valueOf(500),
                            EResource.IRON, BigDecimal.valueOf(500)),
                    new int[]{5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    null);
            case OASIS: return new SettlementEntity(null, null, ENation.GALLS, "Oasis", 0, 0,
                    EVillageType.OASIS, 0, 0, 100,
                    Map.ofEntries(
                            Map.entry(1, new BuildModel(EBuilding.WOODCUTTER, 2)),
                            Map.entry(2, new BuildModel(EBuilding.CROPLAND, 2)),
                            Map.entry(3, new BuildModel(EBuilding.WOODCUTTER, 0)),
                            Map.entry(4, new BuildModel(EBuilding.IRON_MINE, 2)),
                            Map.entry(5, new BuildModel(EBuilding.CLAY_PIT, 2)),
                            Map.entry(6, new BuildModel(EBuilding.CLAY_PIT, 0)),
                            Map.entry(7, new BuildModel(EBuilding.IRON_MINE, 0)),
                            Map.entry(8, new BuildModel(EBuilding.CROPLAND, 0)),
                            Map.entry(9, new BuildModel(EBuilding.CROPLAND, 0)),
                            Map.entry(10, new BuildModel(EBuilding.IRON_MINE, 0)),
                            Map.entry(11, new BuildModel(EBuilding.IRON_MINE, 0)),
                            Map.entry(12, new BuildModel(EBuilding.CROPLAND, 0)),
                            Map.entry(13, new BuildModel(EBuilding.CROPLAND, 0)),
                            Map.entry(14, new BuildModel(EBuilding.CROPLAND, 0)),
                            Map.entry(15, new BuildModel(EBuilding.CLAY_PIT, 0)),
                            Map.entry(16, new BuildModel(EBuilding.WOODCUTTER, 0)),
                            Map.entry(17, new BuildModel(EBuilding.CLAY_PIT, 0)),
                            Map.entry(18, new BuildModel(EBuilding.WOODCUTTER, 0)),

                            Map.entry(19, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(20, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(21, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(22, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(23, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(24, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(25, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(26, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(27, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(28, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(29, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(30, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(31, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(32, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(33, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(34, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(35, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(36, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(37, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(38, new BuildModel(EBuilding.EMPTY, 0)),
                            Map.entry(39, new BuildModel(EBuilding.EMPTY, 0))),
                    Map.of(EResource.CROP, BigDecimal.valueOf(1000),
                            EResource.CLAY, BigDecimal.valueOf(1000),
                            EResource.WOOD, BigDecimal.valueOf(1000),
                            EResource.IRON, BigDecimal.valueOf(1000)),
                    new int[]{5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    null);
            default: return null;
        }
    }

    public static SettlementEntity getOasis(EOasesKind oasisKind){
        var entity = getVillageByType(EVillageType.OASIS);
        switch (oasisKind){
            case CROP: entity.setVillageType(EVillageType.OASIS_CROP);
            break;
            case WOOD: entity.setVillageType(EVillageType.OASIS_WOOD);
                break;
            case IRON: entity.setVillageType(EVillageType.OASIS_IRON);
                break;
            case CLAY: entity.setVillageType(EVillageType.OASIS_CLAY);
                break;
            default: return entity;
        }
        return entity;
    }

}
