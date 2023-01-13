package io.lanu.travian.game.models.battle;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Army {
    private List<Unit> units;
    private List<Integer> numbers;
    private List<Integer> upgrades;

    public Army(Side side) {
        this.units = side.getUnits();
        this.numbers = side.getNumbers();
        this.upgrades = side.getUpgrades();
    }

    public boolean isScan() {
        int scanPosition = 0;
        for (int i = 0; i < units.size(); i++){
            if (units.get(i).getUnitKind().equals(UnitKind.SPY)){
                scanPosition = i;
                break;
            }
        }
        return numbers.get(scanPosition) > 0;
    }

    public BattlePoints getOff() {
        var result = Army.zipWith((unit, number, upgrade) -> {
            var points = number * upgrade(unit.getOffense(), upgrade);
            return BattlePoints.off(points, unit.isInfantry());
        }, units, numbers, upgrades);
        return result.stream().reduce(BattlePoints.zero(), BattlePoints::add);
    }

    public BattlePoints getDef() {
        var result = Army.zipWith(
                (unit, number, upgrade) ->
                        new BattlePoints(
                                upgrade(unit.getDefenseInfantry(), upgrade), upgrade(unit.getDefenseCavalry(), upgrade))
                                .mul(number), units, numbers, upgrades);
        return result.stream().reduce(BattlePoints.zero(), BattlePoints::add);
    }

    public double getScan() {
        var result = Army.zipWith(
                (unit, number, upgrade) ->
                        unit.getUnitKind().equals(UnitKind.SPY) ? number * upgrade(unit.getS(), upgrade) : 0
        , units, numbers, upgrades);
        return result.stream().reduce(0.0, Double::sum);
    }

    public double getScanDef() {
        var result = Army.zipWith(
                (unit, number, upgrade) ->
                        unit.getUnitKind().equals(UnitKind.SPY) ? number * upgrade(unit.getSD(), upgrade) : 0
                , units, numbers, upgrades);
        return result.stream().reduce(0.0, Double::sum);
    }

    public Integer getTotal() {
        return numbers.stream().reduce(0, Integer::sum);
    }

    private double upgrade(int stat, int level){
        double number = Math.pow(1.015, level) * stat;
        return (int) (1e-4 * Math.round(number / 1e-4));
    }

    public static <A, B, C, R> List<R> zipWith(ZipWith<A, B, C, R> f,
                      List<A> units,
                      List<B> numbers,
                      List<C> upgrades){
        var result = new ArrayList<R>();
        for (int i = 0; i < units.size(); i++){
            result.add(f.apply(units.get(i), numbers.get(i), upgrades.get(i)));
        }
        return result;
    }

    public void applyLosses(double losses) {
        numbers = numbers.stream()
                .map(n -> (int) Math.round(n * (1 - losses)))
                .collect(Collectors.toList());
    }

    public int[] rams() {
        for (int i = 0; i < 10; i++) {
            if (units.get(i).getUnitKind().equals(UnitKind.RAM)) {
                return new int[]{numbers.get(i), upgrades.get(i)};
            }
        }
        return new int[]{0, 0};
    }

    public int[] cats() {
        for (int i = 0; i < 10; i++) {
            if (units.get(i).getUnitKind().equals(UnitKind.CAT)) {
                return new int[]{numbers.get(i), upgrades.get(i)};
            }
        }
        return new int[]{0, 0};
    }
}

