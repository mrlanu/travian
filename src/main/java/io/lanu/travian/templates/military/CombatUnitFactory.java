package io.lanu.travian.templates.military;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.enums.ENation;

import java.util.Map;

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

    public static ECombatUnit getCombatUnitFromArrayPosition(int position, ENation nation){
        switch (position){
            case 0: if (nation.equals(ENation.GALLS)){
                return ECombatUnit.PHALANX;
            } else if (nation.equals(ENation.ROME)){
                return null; // here should be Rome unit
            }
            break;
            case 1: if (nation.equals(ENation.GALLS)){
                return ECombatUnit.SWORDSMAN;
            } else if (nation.equals(ENation.ROME)){
                return null; // here should be Rome unit
            }
            break;
            case 2: if (nation.equals(ENation.GALLS)){
                return ECombatUnit.PATHFINDER;
            } else if (nation.equals(ENation.ROME)){
                return null; // here should be Rome unit
            }
                break;
            case 3: if (nation.equals(ENation.GALLS)){
                return ECombatUnit.THUNDER;
            } else if (nation.equals(ENation.ROME)){
                return null; // here should be Rome unit
            }
                break;
            case 4: if (nation.equals(ENation.GALLS)){
                return ECombatUnit.DRUIDRIDER;
            } else if (nation.equals(ENation.ROME)){
                return null; // here should be Rome unit
            }
                break;
            case 5: if (nation.equals(ENation.GALLS)){
                return ECombatUnit.HAEDUAN;
            } else if (nation.equals(ENation.ROME)){
                return null; // here should be Rome unit
            }
                break;
            case 6: if (nation.equals(ENation.GALLS)){
                return ECombatUnit.RAM;
            } else if (nation.equals(ENation.ROME)){
                return null; // here should be Rome unit
            }
                break;
            case 7: if (nation.equals(ENation.GALLS)){
                return ECombatUnit.TREBUCHET;
            } else if (nation.equals(ENation.ROME)){
                return null; // here should be Rome unit
            }
                break;
            case 8: if (nation.equals(ENation.GALLS)){
                return ECombatUnit.CHIEFTAIN;
            } else if (nation.equals(ENation.ROME)){
                return null; // here should be Rome unit
            }
                break;
            case 9: if (nation.equals(ENation.GALLS)){
                return ECombatUnit.SETTLER;
            } else if (nation.equals(ENation.ROME)){
                return null; // here should be Rome unit
            }
                break;
            case 10: if (nation.equals(ENation.GALLS)){
                return ECombatUnit.HERO;
            } else if (nation.equals(ENation.ROME)){
                return null; // here should be Rome unit
            }
                break;
            default: return null;
        }
        return null;
    }
}
