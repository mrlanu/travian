package io.lanu.travian.game.models.battle;

import java.util.Arrays;
import java.util.List;

public class UnitsConst {
    public static final List<List<Unit>> UNITS = List.of(
            Arrays.asList( //ROMANS
                    new Unit(UnitKind.UNIT, "Legionnaire",40, 35, 50, 6,
                            Arrays.asList(120, 100, 150, 30), 1, 2000, 50, true, 7800, ""),
                    new Unit(UnitKind.UNIT, "Praetorian", 30, 65, 35, 5,
                            Arrays.asList(100, 130, 160, 70), 1, 2200, 20, true, 8400, ""),
                    new Unit(UnitKind.UNIT, "Imperian", 70, 40, 25, 7,
                            Arrays.asList(150, 160, 210, 80), 1, 2400, 50, true, 9000, ""),
                    new Unit(UnitKind.SPY, "Equites Legati", 0, 20, 10, 16,
                            Arrays.asList(140, 160, 20, 40), 2, 1700, 0, false, 6900, ""),
                    new Unit(UnitKind.UNIT, "Equites Imperatoris", 120, 65, 50, 14,
                            Arrays.asList(550, 440, 320, 100), 3, 3300, 100, false, 11700, ""),
                    new Unit(UnitKind.UNIT, "Equites Caesaris", 180, 80, 105, 10,
                            Arrays.asList(550, 640, 800, 180), 4, 4400, 70, false, 15000, ""),
                    new Unit(UnitKind.RAM, "Battering ram", 60, 30, 75, 4,
                            Arrays.asList(900, 360, 500, 70), 3, 4600, 0, true, 15600, ""),
                    new Unit(UnitKind.CAT, "Fire Catapult", 75, 60, 10, 3,
                            Arrays.asList(950, 1350, 600, 90), 6, 9000, 0, true, 28800, ""),
                    new Unit(UnitKind.ADMIN, "Senator", 50, 40, 30, 4,
                            Arrays.asList(30750, 27200, 45000, 37500), 5, 90700, 0, true, 24475, ""),
                    new Unit(UnitKind.SETTLER, "Settler", 0, 80, 80, 5,
                            Arrays.asList(4600, 4200, 5800, 4400), 1, 26900, 3000, true, 0, "")
            ),
            List.of(), //TEUTONS
            Arrays.asList( //GAULS
                    new Unit(UnitKind.UNIT, "Phalanx",15, 40, 50, 7,
                            Arrays.asList(100, 130, 55, 30), 1, 1300, 35, true, 5700, ""),
                    new Unit(UnitKind.UNIT, "Swordsman", 65, 35, 20, 6,
                            Arrays.asList(140, 150, 185, 60), 1, 1800, 45, true, 7200, ""),
                    new Unit(UnitKind.SPY, "Pathfinder", 0, 20, 10, 17,
                            Arrays.asList(170, 150, 20, 40), 2, 1700, 0, false, 6900, ""),
                    new Unit(UnitKind.UNIT, "Theutates Thunder", 90, 25, 40, 19,
                            Arrays.asList(350, 450, 230, 60), 2, 3100, 75, false, 11100, ""),
                    new Unit(UnitKind.UNIT, "Druidrider", 45, 115, 55, 16,
                            Arrays.asList(360, 330, 280, 120), 2, 3200, 35, false, 11400, ""),
                    new Unit(UnitKind.UNIT, "Haeduan", 140, 50, 165, 13,
                            Arrays.asList(500, 620, 675, 170), 3, 3900, 65, false, 13500, ""),
                    new Unit(UnitKind.RAM, "Ram", 50, 30, 105, 4,
                            Arrays.asList(950, 555, 330, 75), 3, 5000, 0, false, 16800, ""),
                    new Unit(UnitKind.CAT, "Trebuchet", 70, 45, 10, 3,
                            Arrays.asList(960, 1450, 630, 90), 6, 9000, 0, true, 28800, ""),
                    new Unit(UnitKind.ADMIN, "Chieftain", 40, 50, 50, 5,
                            Arrays.asList(30750, 45400, 31000, 37500), 4, 90700, 0, true, 24475, ""),
                    new Unit(UnitKind.SETTLER, "Settler", 0, 80, 80, 5,
                            Arrays.asList(4400, 5600, 4200, 3900), 1, 22700, 3000, true, 0, "")
                            ),
            Arrays.asList( //NATURE (But here is ROMANS for now, should be changed for real natures units)
                    new Unit(UnitKind.UNIT, "Legionnaire",40, 35, 50, 6,
                            Arrays.asList(120, 100, 150, 30), 1, 2000, 50, true, 7800, ""),
                    new Unit(UnitKind.UNIT, "Praetorian", 30, 65, 35, 5,
                            Arrays.asList(100, 130, 160, 70), 1, 2200, 20, true, 8400, ""),
                    new Unit(UnitKind.UNIT, "Imperian", 70, 40, 25, 7,
                            Arrays.asList(150, 160, 210, 80), 1, 2400, 50, true, 9000, ""),
                    new Unit(UnitKind.SPY, "Equites Legati", 0, 20, 10, 16,
                            Arrays.asList(140, 160, 20, 40), 2, 1700, 0, false, 6900, ""),
                    new Unit(UnitKind.UNIT, "Equites Imperatoris", 120, 65, 50, 14,
                            Arrays.asList(550, 440, 320, 100), 3, 3300, 100, false, 11700, ""),
                    new Unit(UnitKind.UNIT, "Equites Caesaris", 180, 80, 105, 10,
                            Arrays.asList(550, 640, 800, 180), 4, 4400, 70, false, 15000, ""),
                    new Unit(UnitKind.RAM, "Battering ram", 60, 30, 75, 4,
                            Arrays.asList(900, 360, 500, 70), 3, 4600, 0, true, 15600, ""),
                    new Unit(UnitKind.CAT, "Fire Catapult", 75, 60, 10, 3,
                            Arrays.asList(950, 1350, 600, 90), 6, 9000, 0, true, 28800, ""),
                    new Unit(UnitKind.ADMIN, "Senator", 50, 40, 30, 4,
                            Arrays.asList(30750, 27200, 45000, 37500), 5, 90700, 0, true, 24475, ""),
                    new Unit(UnitKind.SETTLER, "Settler", 0, 80, 80, 5,
                            Arrays.asList(4600, 4200, 5800, 4400), 1, 26900, 3000, true, 0, "")
            )
    );
}
