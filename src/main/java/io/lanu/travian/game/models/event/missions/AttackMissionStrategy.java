package io.lanu.travian.game.models.event.missions;

import io.lanu.travian.enums.ECombatGroupMission;
import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.entities.ReportEntity;
import io.lanu.travian.game.entities.ReportPlayerEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.services.EngineService;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Arrays;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttackMissionStrategy extends MissionStrategy {

    public AttackMissionStrategy(SettlementEntity currentSettlement, CombatGroupEntity combatGroup, EngineService engineService) {
        super(currentSettlement, combatGroup, engineService);
    }

    @Override
    public void handle() {
        //here is recursive recalculation of all villages involved in this attack
        // perform an attack if we are in origin village or skip and this attack will be performed in target village during recursion
        if (currentSettlement.getId().equals(combatGroup.getToSettlementId())){

            var storage = currentSettlement.getStorage();

            //here should be an algorithm of a plunder
            // just dummy implementation
            var clay = storage.get(1);
            clay = clay.subtract(BigDecimal.valueOf(100));
            storage.set(1, clay);
            combatGroup.setPlunder(Arrays.asList(BigDecimal.ZERO, BigDecimal.valueOf(100), BigDecimal.ZERO, BigDecimal.ZERO));
            createReports();
            //----------
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

        } else{
            //just in the skip case
            engineService.updateParticularSettlementState(combatGroup.getToSettlementId(), combatGroup.getExecutionTime().plusSeconds(1));
        }
    }

    private void createReports() {
        var settlement = engineService
                .getSettlementRepository().findById(combatGroup.getFromSettlementId()).orElseThrow();
        var report = new ReportEntity(
                settlement.getAccountId(),
                ECombatGroupMission.ATTACK,
                new ReportPlayerEntity(combatGroup.getFromSettlementId(), combatGroup.getOwnerNation(), combatGroup.getUnits(),
                        combatGroup.getUnits(), combatGroup.getPlunder(), 300),
                new ReportPlayerEntity(currentSettlement.getId(), currentSettlement.getNation(), currentSettlement.getHomeLegion(),
                        currentSettlement.getHomeLegion(), null, 0), combatGroup.getExecutionTime());
        var repo = engineService.getReportRepository();
        repo.save(report);
        report.setReportOwner(currentSettlement.getAccountId());
        report.setId(null);
        repo.save(report);
    }
}
