package io.lanu.travian.templates.villages;

import io.lanu.travian.enums.*;
import io.lanu.travian.game.entities.BuildModel;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.buildings.BuildingsID;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class VillageEntityFactory {

    public static SettlementEntity getVillageByType(SettlementType villageType, SettlementSubType subType){
        switch (villageType){
            case VILLAGE: return new SettlementEntity(null, SettlementType.VILLAGE, subType, null, null, 0, 0 , "New Village", ENation.GALLS,
                    0, 100, 100,
                    Map.ofEntries(
                            Map.entry(1, new BuildModel(BuildingsID.WOODCUTTER, 1)),
                            Map.entry(2, new BuildModel(BuildingsID.CROPLAND, 1)),
                            Map.entry(3, new BuildModel(BuildingsID.WOODCUTTER, 0)),
                            Map.entry(4, new BuildModel(BuildingsID.IRON_MINE, 0)),
                            Map.entry(5, new BuildModel(BuildingsID.CLAY_PIT, 1)),
                            Map.entry(6, new BuildModel(BuildingsID.CLAY_PIT, 1)),
                            Map.entry(7, new BuildModel(BuildingsID.IRON_MINE, 1)),
                            Map.entry(8, new BuildModel(BuildingsID.CROPLAND, 0)),
                            Map.entry(9, new BuildModel(BuildingsID.CROPLAND, 0)),
                            Map.entry(10, new BuildModel(BuildingsID.IRON_MINE, 1)),
                            Map.entry(11, new BuildModel(BuildingsID.IRON_MINE, 1)),
                            Map.entry(12, new BuildModel(BuildingsID.CROPLAND, 1)),
                            Map.entry(13, new BuildModel(BuildingsID.CROPLAND, 0)),
                            Map.entry(14, new BuildModel(BuildingsID.CROPLAND, 0)),
                            Map.entry(15, new BuildModel(BuildingsID.CLAY_PIT, 1)),
                            Map.entry(16, new BuildModel(BuildingsID.WOODCUTTER, 0)),
                            Map.entry(17, new BuildModel(BuildingsID.CLAY_PIT, 0)),
                            Map.entry(18, new BuildModel(BuildingsID.WOODCUTTER, 1)),

                            Map.entry(19, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(20, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(21, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(22, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(23, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(24, new BuildModel(BuildingsID.BARRACK, 1)),
                            Map.entry(25, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(26, new BuildModel(BuildingsID.MAIN, 1)),
                            Map.entry(27, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(28, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(29, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(30, new BuildModel(BuildingsID.GRANARY, 1)),
                            Map.entry(31, new BuildModel(BuildingsID.RALLY_POINT, 1)),
                            Map.entry(32, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(33, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(34, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(35, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(36, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(37, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(38, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(39, new BuildModel(BuildingsID.EMPTY, 0))),
                    Arrays.asList(BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), BigDecimal.valueOf(1000)),
                    Arrays.asList(50, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    null);
            case OASIS: return new SettlementEntity(null, SettlementType.OASIS, subType, null, "Nature", 0, 0 , "Oasis", ENation.NATURE,
                    0, 0, 100,
                    Map.ofEntries(
                            Map.entry(1, new BuildModel(BuildingsID.WOODCUTTER, 2)),
                            Map.entry(2, new BuildModel(BuildingsID.CROPLAND, 2)),
                            Map.entry(3, new BuildModel(BuildingsID.WOODCUTTER, 0)),
                            Map.entry(4, new BuildModel(BuildingsID.IRON_MINE, 2)),
                            Map.entry(5, new BuildModel(BuildingsID.CLAY_PIT, 2)),
                            Map.entry(6, new BuildModel(BuildingsID.CLAY_PIT, 0)),
                            Map.entry(7, new BuildModel(BuildingsID.IRON_MINE, 0)),
                            Map.entry(8, new BuildModel(BuildingsID.CROPLAND, 0)),
                            Map.entry(9, new BuildModel(BuildingsID.CROPLAND, 0)),
                            Map.entry(10, new BuildModel(BuildingsID.IRON_MINE, 0)),
                            Map.entry(11, new BuildModel(BuildingsID.IRON_MINE, 0)),
                            Map.entry(12, new BuildModel(BuildingsID.CROPLAND, 0)),
                            Map.entry(13, new BuildModel(BuildingsID.CROPLAND, 0)),
                            Map.entry(14, new BuildModel(BuildingsID.CROPLAND, 0)),
                            Map.entry(15, new BuildModel(BuildingsID.CLAY_PIT, 0)),
                            Map.entry(16, new BuildModel(BuildingsID.WOODCUTTER, 0)),
                            Map.entry(17, new BuildModel(BuildingsID.CLAY_PIT, 0)),
                            Map.entry(18, new BuildModel(BuildingsID.WOODCUTTER, 0)),

                            Map.entry(19, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(20, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(21, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(22, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(23, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(24, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(25, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(26, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(27, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(28, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(29, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(30, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(31, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(32, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(33, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(34, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(35, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(36, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(37, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(38, new BuildModel(BuildingsID.EMPTY, 0)),
                            Map.entry(39, new BuildModel(BuildingsID.EMPTY, 0))),
                    Arrays.asList(BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), BigDecimal.valueOf(1000)),
                    Arrays.asList(5, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    null);
            default: return null;
        }
    }
}
