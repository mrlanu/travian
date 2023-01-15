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
        List<CombatGroupEntity> sidesEntity = new ArrayList<>();
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
                .numbers(Arrays.stream(combatGroup.getUnits()).boxed().collect(Collectors.toList()))
                .mission(combatGroup.getMission())
                .build();

        var ownDefEntity = CombatGroupEntity.builder()
                .ownerNation(state.getSettlementEntity().getNation())
                .units(state.getSettlementEntity().getHomeLegion())
                .build();
        var ownDef = Army.builder()
                .side(Army.ESide.DEF)
                .population(state.getSettlementEntity().getPopulation())
                .units(UnitsConst.UNITS.get(state.getSettlementEntity().getNation().ordinal()))
                .numbers(Arrays.stream(state.getSettlementEntity().getHomeLegion()).boxed().collect(Collectors.toList()))
                .build();

        var defFromOthers = state.getCombatGroupsInSettlement().stream()
                .map(g -> Army.builder()
                        .side(Army.ESide.DEF)
                        .population(100)
                        .units(UnitsConst.UNITS.get(g.getOwnerNation().ordinal()))
                        .numbers(Arrays.stream(g.getUnits()).boxed().collect(Collectors.toList()))
                        .build()).collect(Collectors.toList());

        //attacker
        sidesEntity.add(combatGroup);
        //defenders
        sidesEntity.add(ownDefEntity);
        sidesEntity.addAll(state.getCombatGroupsInSettlement());

        //defenders
        sidesArmy.add(ownDef);
        sidesArmy.addAll(defFromOthers);
        //attacker
        sidesArmy.add(off);

        var unitsBefore = sidesEntity.stream()
                .map(cG -> cG.getUnits().clone())
                .collect(Collectors.toList());

        battle.perform(battleField, sidesArmy);
        applyLosses(sidesEntity, battle.getBattleResult());

        var casualties = new ArrayList<int[]>();
        for (int i = 0; i < sidesEntity.size(); i++){
            int[] cas = new int[10];
            int[] unitsBf = unitsBefore.get(i);
            int[] unitsAfter = sidesEntity.get(i).getUnits();
            for (int j = 0; j < cas.length; j++){
                cas[j] = unitsBf[j] - unitsAfter[j];
            }
            casualties.add(cas);
        }

        //if attacker hasn't been destroyed completely
        if (Arrays.stream(combatGroup.getUnits()).sum() > 0){
            returnOff(attackingSettlement, unitsBefore, casualties);
        }else {
            engineService.getCombatGroupRepository().deleteById(combatGroup.getId());
            createReports(attackingSettlement, unitsBefore, casualties,
                    Arrays.asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        }
    }

    private void returnOff(SettlementEntity attackingSettlement, List<int[]> unitsBefore, List<int[]> casualtiesUnits) {
        List<BigDecimal> plunder = calculatePlunder();
        createReports(attackingSettlement, unitsBefore, casualtiesUnits, plunder);
        subtractStolenResources(plunder);
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

    private void applyLosses(List<CombatGroupEntity> combatGroups, BattleResult battleResult) {
        var offUnits = combatGroups.get(0).getUnits();
        for (int i = 0; i < offUnits.length; i++){
            offUnits[i] = (int) Math.round(offUnits[i] * (1 - battleResult.getOffLoses()));
        }
        combatGroups.stream().skip(1).forEachOrdered(cG -> {
            var defUnits = cG.getUnits();
            for (int i = 0; i < defUnits.length; i++){
                defUnits[i] = (int) Math.round(defUnits[i] * (1 - battleResult.getDefLosses()));
            }
        });
    }

    private void createReports(SettlementEntity attacker,
                               List<int[]> unitsBefore,
                               List<int[]> casualtiesUnits,
                               List<BigDecimal> plunder) {
        var currentSettlement = state.getSettlementEntity();
        int carry = calculateCarry();
        var report = new ReportEntity(
                attacker.getAccountId(),
                combatGroup.getMission(),
                ReportPlayerEntity.builder()
                        .settlementId(combatGroup.getFromSettlementId())
                        .nation(combatGroup.getOwnerNation())
                        .troops(unitsBefore.get(0))
                        .dead(casualtiesUnits.get(0))
                        .bounty(plunder)
                        .carry(carry)
                        .build(),
                ReportPlayerEntity.builder()
                        .settlementId(currentSettlement.getId())
                        .nation(currentSettlement.getNation())
                        .troops(unitsBefore.get(1))
                        .dead(casualtiesUnits.get(1))
                        .build(), combatGroup.getExecutionTime());
        var repo = engineService.getReportRepository();
        repo.save(report);
        report.setReportOwner(currentSettlement.getAccountId());
        report.setId(null);
        repo.save(report);
    }

    private int calculateCarry() {
        var units = UnitsConst.UNITS.get(combatGroup.getOwnerNation().ordinal());
        var carry = 0;
        for (int i = 0; i < combatGroup.getUnits().length; i++){
            carry += combatGroup.getUnits()[i] * units.get(i).getCapacity();
        }
        return carry;
    }
}
