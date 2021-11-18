package io.lanu.travian.templates.military;

import io.lanu.travian.enums.EUnits;

public class MilitaryUnitsFactory {

    public static EUnits getUnit(String name, int level){
        EUnits result;
        switch (name){
            case "Phalanx": result = EUnits.PHALANX;
                break;
            case "Legionnaire": result = EUnits.LEGIONNAIRE;
                break;
            default: return null;
        }
        result.setLevel(level);
        //here should be implemented the time counting depends on level of barracks
        result.setTime(result.getTime());
        return result;
    }
}
