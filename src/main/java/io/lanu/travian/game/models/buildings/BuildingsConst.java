package io.lanu.travian.game.models.buildings;

import io.lanu.travian.enums.EBuildingType;
import io.lanu.travian.templates.Time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildingsConst {

    private static final Integer[] productions = {
            2, 5, 9, 15, 22, 33, 50, 70, 100, 145, 200,
            280, 375, 495, 635, 800, 1000, 1300, 1600,
            2000, 2450, 3050};

    private static double getProduction(int level){
        return (double)productions[level];
    }

    private static double getCapacity(int level) {
        double number = Math.pow(1.2, level) * 2120 - 1320;
        return (int) (100.0 * Math.round(number / 100.0));
    }

    private static double id(int level) {
        return (double) level;
    }

    private static double mbLike(int level){
        return Math.pow(0.964, (level - 1));
    }

    private static double train(int level){
        return Math.pow(0.9, (level - 1));
    }

    public static final List<Building> BUILDINGS =
            Arrays.asList(
                    Building.builder()
                            .id(BuildingsID.WOODCUTTER)
                            .name("Wood cutter")
                            .type(EBuildingType.RESOURCE)
                            .cost(Arrays.asList(40, 100, 50, 60))
                            .benefit(BuildingsConst::getProduction)
                            .k(1.67)
                            .upkeep(2)
                            .culture(1)
                            .time(new Time(1780/3,1.6, 1000/3))
                            .maxLevel(20)
                            .description("")
                            .requirementBuildings(new ArrayList<>())
                            .isMulti(true)
                            .build(),
                    Building.builder()
                            .id(BuildingsID.CLAY_PIT)
                            .name("Clay pit")
                            .type(EBuildingType.RESOURCE)
                            .cost(Arrays.asList(80, 40, 80, 50))
                            .benefit(BuildingsConst::getProduction)
                            .k(1.67)
                            .upkeep(2)
                            .culture(1)
                            .time(new Time(1660/3,1.6, 1000/3))
                            .maxLevel(22)
                            .description("")
                            .requirementBuildings(new ArrayList<>())
                            .isMulti(true)
                            .build(),
                    Building.builder()
                            .id(BuildingsID.IRON_MINE)
                            .name("Iron mine")
                            .type(EBuildingType.RESOURCE)
                            .cost(Arrays.asList(100, 80, 30, 60))
                            .benefit(BuildingsConst::getProduction)
                            .k(1.67)
                            .upkeep(2)
                            .culture(1)
                            .time(new Time(2350/3,1.6, 1000/3))
                            .maxLevel(22)
                            .description("")
                            .requirementBuildings(new ArrayList<>())
                            .isMulti(true)
                            .build(),
                    Building.builder()
                            .id(BuildingsID.CROPLAND)
                            .name("Cropland")
                            .type(EBuildingType.RESOURCE)
                            .cost(Arrays.asList(70, 90, 70, 20))
                            .benefit(BuildingsConst::getProduction)
                            .k(1.67)
                            .upkeep(0)
                            .culture(1)
                            .time(new Time(1450/3,1.6, 1000/3))
                            .maxLevel(22)
                            .description("")
                            .requirementBuildings(new ArrayList<>())
                            .isMulti(true)
                            .build(),
                    Building.builder()
                            .id(BuildingsID.RALLY_POINT)
                            .name("Rally-point")
                            .type(EBuildingType.MILITARY)
                            .cost(Arrays.asList(110, 160, 90, 70))
                            .benefit(BuildingsConst::id)
                            .k(1.28)
                            .upkeep(1)
                            .culture(1)
                            .time(new Time(3875))
                            .maxLevel(20)
                            .description("")
                            .requirementBuildings(new ArrayList<>())
                            .isMulti(false)
                            .build(),
                    Building.builder()
                            .id(BuildingsID.MAIN)
                            .name("Main-building")
                            .type(EBuildingType.INFRASTRUCTURE)
                            .cost(Arrays.asList(70, 40, 60, 20))
                            .benefit(BuildingsConst::mbLike)
                            .k(1.28)
                            .upkeep(2)
                            .culture(2)
                            .time(new Time(3875))
                            .maxLevel(20)
                            .description("")
                            .requirementBuildings(new ArrayList<>())
                            .isMulti(false)
                            .build(),
                    Building.builder()
                            .id(BuildingsID.GRANARY)
                            .name("Granary")
                            .type(EBuildingType.INFRASTRUCTURE)
                            .cost(Arrays.asList(80, 100, 70, 20))
                            .benefit(BuildingsConst::getCapacity)
                            .k(1.28)
                            .upkeep(1)
                            .culture(1)
                            .time(new Time(3475))
                            .maxLevel(20)
                            .description("")
                            .requirementBuildings(
                                    List.of(new RequirementBuilding(BuildingsID.MAIN, "Main building", 1)))
                            .isMulti(true)
                            .build(),
                    Building.builder()
                            .id(BuildingsID.WAREHOUSE)
                            .name("Warehouse")
                            .type(EBuildingType.INFRASTRUCTURE)
                            .cost(Arrays.asList(130, 160, 90, 40))
                            .benefit(BuildingsConst::getCapacity)
                            .k(1.28)
                            .upkeep(1)
                            .culture(1)
                            .time(new Time(3875))
                            .maxLevel(20)
                            .requirementBuildings(
                                    List.of(new RequirementBuilding(BuildingsID.MAIN, "Main building", 1)))
                            .isMulti(true)
                            .build(),
                    Building.builder()
                            .id(BuildingsID.BARRACK)
                            .name("Barrack")
                            .type(EBuildingType.MILITARY)
                            .cost(Arrays.asList(210, 140, 260, 120))
                            .benefit(BuildingsConst::train)
                            .k(1.28)
                            .upkeep(4)
                            .culture(1)
                            .time(new Time(3875))
                            .maxLevel(20)
                            .description("")
                            .requirementBuildings(Arrays.asList(
                                    new RequirementBuilding(BuildingsID.MAIN, "Main building", 3),
                                    new RequirementBuilding(BuildingsID.WAREHOUSE, "Warehouse", 1),
                                    new RequirementBuilding(BuildingsID.GRANARY, "Granary", 1)))
                            .isMulti(false)
                            .build(),
                    Building.builder()
                            .id(BuildingsID.MARKETPLACE)
                            .name("Marketplace")
                            .type(EBuildingType.INFRASTRUCTURE)
                            .cost(Arrays.asList(80,  70, 120,  70))
                            .benefit(BuildingsConst::id)
                            .k(1.28)
                            .upkeep(4)
                            .culture(3)
                            .time(new Time(3675))
                            .description("")
                            .requirementBuildings(Arrays.asList(
                                    new RequirementBuilding(BuildingsID.MAIN, "Main building", 1),
                                    new RequirementBuilding(BuildingsID.WAREHOUSE, "Warehouse", 1),
                                    new RequirementBuilding(BuildingsID.GRANARY, "Granary", 1)))
                            .isMulti(false)
                            .build(),
                    Building.builder()
                            .id(BuildingsID.ACADEMY)
                            .name("Academy")
                            .type(EBuildingType.MILITARY)
                            .cost(Arrays.asList(220, 160,  90,  40))
                            .benefit(BuildingsConst::mbLike)
                            .k(1.28)
                            .upkeep(4)
                            .culture(4)
                            .time(new Time(3875))
                            .maxLevel(20)
                            .requirementBuildings(Arrays.asList(
                                    new RequirementBuilding(BuildingsID.BARRACK, "Barrack", 3),
                                    new RequirementBuilding(BuildingsID.MAIN, "Main building", 3)))
                            .isMulti(false)
                            .build(),
                    Building.builder()
                            .id(BuildingsID.BLACKSMITH)
                            .name("Blacksmith")
                            .type(EBuildingType.MILITARY)
                            .cost(Arrays.asList(170,  200, 380,  130))
                            .benefit(BuildingsConst::mbLike)
                            .k(1.28)
                            .upkeep(4)
                            .culture(2)
                            .time(new Time(3875))
                            .maxLevel(20)
                            .description("")
                            .requirementBuildings(Arrays.asList(
                                    new RequirementBuilding(BuildingsID.MAIN, "Main building", 3),
                                    new RequirementBuilding(BuildingsID.ACADEMY, "Academy", 1)))
                            .isMulti(false)
                            .build(),
                    Building.builder()
                            .id(BuildingsID.EMPTY)
                            .name("Empty")
                            .type(EBuildingType.EMPTY)
                            .build()
    );
}
