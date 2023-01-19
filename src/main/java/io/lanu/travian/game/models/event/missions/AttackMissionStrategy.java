package io.lanu.travian.game.models.event.missions;

import io.lanu.travian.enums.ECombatGroupMission;
import io.lanu.travian.game.dto.SettlementStateDTO;
import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.entities.ReportEntity;
import io.lanu.travian.game.entities.ReportPlayerEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.battle.*;
import io.lanu.travian.game.services.EngineService;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttackMissionStrategy extends MissionStrategy {

    public AttackMissionStrategy(SettlementStateDTO state, CombatGroupEntity combatGroup, EngineService engineService) {
        super(state, combatGroup, engineService);
    }

    @Override
    public void handle() {
        //here is recursive recalculation of all villages involved in this attack
        // perform an attack if we are in origin village or skip and this attack will be performed in target village during recursion
        if (state.getSettlementEntity().getId().equals(combatGroup.getToSettlementId())){
            executeBattle();
        } else{
            //just in the skip case
            engineService.updateParticularSettlementState(combatGroup.getToSettlementId(), combatGroup.getExecutionTime().plusSeconds(1));
        }
    }

    private void executeBattle() {
        var battle = new Battle();
        List<Army> sidesArmy = new ArrayList<>();

        var attackingSettlement = engineService.getSettlementRepository()
                .findById(combatGroup.getFromSettlementId()).orElseThrow();

        var battleField = BattleField.builder()
                .tribe(state.getSettlementEntity().getNation().ordinal())
                .population(state.getSettlementEntity().getPopulation())
                .build();

        var off = Army.builder()
                .side(Army.ESide.OFF)
                .population(attackingSettlement.getPopulation())
                .units(UnitsConst.UNITS.get(attackingSettlement.getNation().ordinal()))
                .numbers(combatGroup.getUnits())
                .mission(combatGroup.getMission())
                .build();

        var ownDef = Army.builder()
                .side(Army.ESide.DEF)
                .population(state.getSettlementEntity().getPopulation())
                .units(UnitsConst.UNITS.get(state.getSettlementEntity().getNation().ordinal()))
                .numbers(state.getSettlementEntity().getHomeLegion())
                .build();

        var reinforcementEntities = engineService.getCombatGroupRepository()
                .getAllByToSettlementIdAndMoved(state.getSettlementEntity().getId(), false);

        var reinforcementArmies = reinforcementEntities.stream()
                .map(g -> Army.builder()
                        .side(Army.ESide.DEF)
                        .population(100)
                        .units(UnitsConst.UNITS.get(g.getOwnerNation().ordinal()))
                        .numbers(g.getUnits())
                        .build()).collect(Collectors.toList());

        //defenders
        sidesArmy.add(ownDef);
        sidesArmy.addAll(reinforcementArmies);
        //attacker
        sidesArmy.add(off);

        var battleResults = battle.perform(battleField, sidesArmy);
        var plunder = returnOff(sidesArmy.get(sidesArmy.size() - 1));
        sidesArmy.remove(sidesArmy.size() - 1);
        updateDef(sidesArmy, reinforcementEntities);
        createReports(attackingSettlement, reinforcementEntities, battleResults.get(0), plunder);
    }

    private List<BigDecimal> returnOff(Army offArmy) {
        //off has been completely destroyed
        if (offArmy.getNumbers().stream().reduce(0, Integer::sum) == 0){
            engineService.getCombatGroupRepository().deleteById(combatGroup.getId());
            return Arrays.asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        List<BigDecimal> plunder = calculatePlunder();
        subtractStolenResources(plunder);
        combatGroup.setUnits(offArmy.getNumbers());
        combatGroup.setPlunder(plunder);
        combatGroup.setMission(ECombatGroupMission.BACK);
        //swap
        String temp = combatGroup.getFromSettlementId();
        combatGroup.setFromSettlementId(combatGroup.getToSettlementId());
        combatGroup.setToSettlementId(temp);
        String tempAcc = combatGroup.getFromAccountId();
        combatGroup.setFromAccountId(combatGroup.getToAccountId());
        combatGroup.setToAccountId(tempAcc);
        combatGroup.setExecutionTime(combatGroup.getExecutionTime().plusSeconds(combatGroup.getDuration()));
        engineService.getCombatGroupRepository().save(combatGroup);

        return plunder;
    }

    private void updateDef(List<Army> sidesArmy, List<CombatGroupEntity> defEntities) {
        state.getSettlementEntity().setHomeLegion(sidesArmy.get(0).getNumbers());
        // start from 1 because there is off army on the front in sidesArmy
        for (int i = 1; i < sidesArmy.size(); i++){
            var currentDef = defEntities.get(i - 1);
            if (sidesArmy.get(i).getNumbers().stream().reduce(0, Integer::sum) == 0){
                engineService.getCombatGroupRepository().deleteById(currentDef.getId());
                continue;
            }
            currentDef.setUnits(sidesArmy.get(i).getNumbers());
            engineService.getCombatGroupRepository().save(currentDef);
        }
    }

    private void subtractStolenResources(List<BigDecimal> plunder) {
        var storage = state.getSettlementEntity().getStorage();
        IntStream.range(0,4).forEach(i -> {
            var res = storage.get(i);
            res = res.subtract(plunder.get(i));
            storage.set(i, res);
        });
    }

    private List<BigDecimal> calculatePlunder() {
        var storage = state.getSettlementEntity().getStorage();
        var availableResources = storage.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var resPercents = storage.stream()
                .map(res -> BigDecimal
                        .valueOf(100).multiply(res).divide(availableResources, MathContext.DECIMAL32))
                .collect(Collectors.toList());
        var carry = Math.min(calculateCarry(), availableResources.intValue());

        var wood = resPercents.get(0).divide(BigDecimal.valueOf(100), MathContext.DECIMAL32)
                .multiply(BigDecimal.valueOf(carry)).setScale(0, RoundingMode.HALF_DOWN);
        var clay = resPercents.get(1).divide(BigDecimal.valueOf(100), MathContext.DECIMAL32)
                .multiply(BigDecimal.valueOf(carry)).setScale(0, RoundingMode.HALF_DOWN);
        var iron = resPercents.get(2).divide(BigDecimal.valueOf(100), MathContext.DECIMAL32)
                .multiply(BigDecimal.valueOf(carry)).setScale(0, RoundingMode.HALF_DOWN);
        var crop = resPercents.get(3).divide(BigDecimal.valueOf(100), MathContext.DECIMAL32)
                .multiply(BigDecimal.valueOf(carry)).setScale(0, RoundingMode.HALF_DOWN);

        return Arrays.asList(wood, clay, iron, crop);
    }

    private void createReports(SettlementEntity attacker,
                               List<CombatGroupEntity> reinforcement,
                               BattleResult battleResult,
                               List<BigDecimal> plunder) {
        var battleField = state.getSettlementEntity();
        int carry = calculateCarry();
        List<ReportPlayerEntity> defenders = new ArrayList<>();
        //own def
        defenders.add(ReportPlayerEntity.builder()
                .settlementId(battleField.getId())
                .nation(battleField.getNation())
                .troops(battleResult.getUnitsBeforeBattle().get(0))
                .dead(battleResult.getCasualties().get(0))
                .build());
        for (int i = 0; i < reinforcement.size(); i++){
            var current = reinforcement.get(i);
            defenders.add(ReportPlayerEntity.builder()
                    .settlementId(current.getFromSettlementId())
                    .nation(current.getOwnerNation())
                    .troops(battleResult.getUnitsBeforeBattle().get(i + 1))
                    .dead(battleResult.getCasualties().get(i + 1))
                    .build());
        }
        var report = new ReportEntity(
                attacker.getAccountId(),
                combatGroup.getMission(),
                ReportPlayerEntity.builder()
                        //here is to instead of from because swap was apply in the returnOff method
                        .settlementId(combatGroup.getFromSettlementId())
                        .nation(combatGroup.getOwnerNation())
                        .troops(battleResult.getUnitsBeforeBattle().get(battleResult.getUnitsBeforeBattle().size() - 1))
                        .dead(battleResult.getCasualties().get(battleResult.getCasualties().size() - 1))
                        .bounty(plunder)
                        .carry(carry)
                        .build(),
                defenders, combatGroup.getExecutionTime());
        var repo = engineService.getReportRepository();
        repo.save(report);
        report.setReportOwner(battleField.getAccountId());
        report.setId(null);
        repo.save(report);
    }

    private int calculateCarry() {
        var units = UnitsConst.UNITS.get(combatGroup.getOwnerNation().ordinal());
        var carry = 0;
        for (int i = 0; i < combatGroup.getUnits().size(); i++){
            carry += combatGroup.getUnits().get(i) * units.get(i).getCapacity();
        }
        return carry;
    }
}
