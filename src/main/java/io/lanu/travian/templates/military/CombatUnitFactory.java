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

    public static List<Integer> mapHomeArmyToList(Map<ECombatUnit, Integer> armyMap){
        var result = new ArrayList<Integer>();
        IntStream.range(0, 10).forEach(i -> result.add(0));
        armyMap.forEach((k, v) -> {
            switch (k){
                case PHALANX: result.set(0, v);
                    break;
                case SWORDSMAN: result.set(1, v);
                    break;
                case PATHFINDER: result.set(2, v);
                    break;
                case THUNDER: result.set(3, v);
                    break;
                case DRUIDRIDER: result.set(4, v);
                    break;
                case HAEDUAN: result.set(5, v);
                    break;
                case RAM: result.set(6, v);
                    break;
                case TREBUCHET: result.set(7, v);
                    break;
                case CHIEFTAIN: result.set(8, v);
                    break;
                case SETTLER: result.set(9, v);
                    break;
            }
        });
        return result;
    }
}
