package io.lanu.travian.game.models.battle;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Fns {

    public BattleSides<Double> adducedDef(BattlePoints off, BattlePoints def) {
        var totalOff = off.getI() + off.getC();
        var infantryPart = roundPercent(off.getI() / totalOff);
        var cavalryPart = roundPercent(off.getC() / totalOff);
        var totalDef = def.getI() * infantryPart + def.getC() * cavalryPart;
        return BattleSides.off(totalOff, totalDef);
    }

    private double roundPercent(double number) {
        return Fns.roundP(1e-4, number);
    }

    public double immensity(Integer total) {
        return 1.5;
    }

    public double morale(int offPop, int defPop, double ptsRatio) {
        if (offPop <= defPop) { return 1; }
        var popRatio = offPop / Math.max(defPop, 3);
        return Math.max(0.667, Fns.roundP(1e-3, Math.pow(popRatio, -0.2 * Math.min(ptsRatio, 1))));
    }

    public static double roundP(double precision, double number){
        return precision * Math.round(number / precision);
    }

    public BattleSides<Double> raid(double x) {
        return BattleSides.off(1 / (1 + x), x / (1 + x));
    }
}

