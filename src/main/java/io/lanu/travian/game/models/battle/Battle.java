package io.lanu.travian.game.models.battle;

import io.lanu.travian.enums.ECombatGroupMission;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Battle {
    private BattleField battleField;
    private Army offArmy;
    private Side offSide;
    private List<Army> defArmies;
    private final int BASE_VILLAGE_DEF = 10;
    private BattleState battleState;
    private BattleResult battleResult;
    private Fns fns;

    public Battle() {
        this.defArmies = new ArrayList<>();
        this.battleState = new BattleState();
        this.battleResult = new BattleResult();
        this.fns = new Fns();
    }

    public List<BattleResult> perform(BattleField battleField, List<Side> sides){
        this.battleField = battleField;
        var results = new ArrayList<BattleResult>();
        for (Side side : sides){
            if (side.getSide().equals(Side.ESide.DEF)){
                defArmies.add(new Army(side));
            } else {
                offSide = side;
                offArmy = new Army(side);
                wave();
                defArmies.forEach(d -> d.applyLosses(battleResult.getDefLosses()));
                offArmy.applyLosses(battleResult.getOffLoses());
                results.add(battleResult);
            }
        }
        return results;
    }
    private void wave() {
        battleResult = new BattleResult(0, 0, new ArrayList<>(), battleField.getWall().getLevel());
        battleState.setWall(battleField.getWall().getLevel());
        if (offArmy.isScan()){
            scan();
        }else if (offSide.getMission().equals(ECombatGroupMission.RAID)){
            raid();
        }else {
            normal();
        }
        loneAttackerDies();
    }

    private void scan() {
        var offPoints = offArmy.getScan() * morale(false);
        var defPoints = defArmies.stream()
                .reduce(0.0, (a, b) -> a + b.getScanDef(), Double::sum) * getDefBonus();
        var losses = Math.min(Math.pow((defPoints / offPoints), 1.5), 1);
        battleResult.setOffLoses(losses);
        battleResult.setDefLosses(0.0);
    }

    private void raid() {
        calcBasePoints();
        calcTotalPoints();
        var x = calcRatio();
        var pair = fns.raid(x);
        battleResult.setOffLoses(pair.getOff());
        battleResult.setDefLosses(pair.getDef());
    }

    private double calcRatio() {
        return Math.pow(battleState.getRatio(),  battleState.getImmensity());
    }

    private void normal() {
    }

    private void loneAttackerDies() {
        if (offArmy.getTotal() == 1) {
            var offPair = offArmy.getOff();
            var off = offPair.getI() + offPair.getC();
            var morale = morale(false);
            if (off * morale < 84.5) { battleResult.setOffLoses(1); }
        }
    }

    private void calcBasePoints() {
        var offPts = offArmy.getOff();
        var defPts = defArmies.stream().map(Army::getDef).reduce(BattlePoints::add).get();
        battleState.setBase(fns.adducedDef(offPts, defPts));
        var total = defArmies.stream().reduce(offArmy.getTotal(), (i, a) -> i + a.getTotal(), Integer::sum);
        battleState.setImmensity(fns.immensity(total));
    }

    private void calcTotalPoints() {
        calcDefBonuses();
        battleState.getFinale().setOff(battleState.getBase().getOff());
        var morale = morale(true);
        battleState.getFinale().setOff(battleState.getBase().getOff() * morale);
    }

    private double morale(boolean remorale) {
        return fns.morale(
                offSide.getPopulation(),
                battleField.getPopulation(),
                remorale ? (battleState.getFinale().getOff() / battleState.getFinale().getDef()) : 1.0);
    }

    private void calcDefBonuses() {
        var result = (this.battleState.getBase().getDef() + getDefAbsolute()) * getDefBonus();
        battleState.getFinale().setDef(result);
    }

    private double getDefBonus() {
        return 1 + battleField.getWall().getBonus(1.025).getDefBonus();
    }

    private Double getDefAbsolute() {
        return BASE_VILLAGE_DEF
                + (battleField.getDef()) + battleField.getWall().getBonus(1.025).getDef()
                + (battleField.getWall().getBonus(1.025).getDef());
    }
}
