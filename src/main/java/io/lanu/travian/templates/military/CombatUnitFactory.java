package io.lanu.travian.templates.military;

import io.lanu.travian.enums.ECombatUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class CombatUnitFactory {

    public static ECombatUnit getUnit(String name, int level){
        ECombatUnit result;
        switch (name){
            case "Phalanx": result = ECombatUnit.PHALANX;
                break;
            default: return null;
        }
        result.setLevel(level);
        //here should be implemented the time counting depends on level of barracks
        result.setTime(result.getTime());
        return result;
    }

    public static int[] mapHomeArmyToIntArray(Map<ECombatUnit, Integer> armyMap){
        var result = new int[11];
        armyMap.forEach((k, v) -> {
            switch (k){
                case PHALANX: result[0] = v;
                    break;
                case SWORDSMAN: result[1] = v;
                    break;
                case PATHFINDER: result[2] = v;
                    break;
                case THUNDER: result[3] = v;
                    break;
                case DRUIDRIDER: result[4] = v;
                    break;
                case HAEDUAN: result[5] = v;
                    break;
                case RAM: result[6] = v;
                    break;
                case TREBUCHET: result[7] = v;
                    break;
                case CHIEFTAIN: result[8] = v;
                    break;
                case SETTLER: result[9] = v;
                    break;
                case HERO: result[10] = v;
                break;
            }
        });
        return result;
    }
}
