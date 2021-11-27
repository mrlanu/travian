package io.lanu.travian.templates.military;

import io.lanu.travian.enums.ECombatUnit;

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
}
