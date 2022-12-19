package io.lanu.travian.game.models.event.missions;

import io.lanu.travian.enums.ECombatGroupMission;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.entities.ReportEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.ReportPlayerEntity;
import io.lanu.travian.game.services.SettlementState;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttackMissionStrategy extends MissionStrategy {

    public AttackMissionStrategy(SettlementEntity currentSettlement, CombatGroupEntity combatGroup, SettlementState settlementState) {
        super(currentSettlement, combatGroup, settlementState);
    }

    @Override
    public void handle() {
        //here is recursive recalculation of all villages involved in this attack
        // perform an attack if we are in origin village or skip and this attack will be performed in target village during recursion
        if (currentSettlement.getId().equals(combatGroup.getToSettlementId())){

            var storage = currentSettlement.getStorage();

            //here should be an algorithm of a plunder
            // just dummy implementation
            storage.put(EResource.CLAY, storage.get(EResource.CLAY).subtract(BigDecimal.valueOf(100)));
            combatGroup.setPlunder(Map.of(EResource.CROP, BigDecimal.ZERO, EResource.CLAY, BigDecimal.valueOf(100),
                    EResource.IRON, BigDecimal.ZERO, EResource.WOOD, BigDecimal.ZERO));
            //----------
            combatGroup.setMission(ECombatGroupMission.BACK);
            combatGroup.setToSettlementId(combatGroup.getOwnerSettlementId());
            combatGroup.setExecutionTime(LocalDateTime.now().plusSeconds(combatGroup.getDuration()));
            settlementState.getCombatGroupRepository().save(combatGroup);
            createReports();

        } else{

            System.out.println("Skipped " + currentSettlement.getId());
            //just in the skip case
            settlementState.recalculateCurrentState(combatGroup.getToSettlementId());

        }
    }

    private void createReports() {
        var settlement = settlementState
                .getSettlementRepository().findById(combatGroup.getOwnerSettlementId()).orElseThrow();
        var report = new ReportEntity(
                settlement.getAccountId(),
                ECombatGroupMission.ATTACK,
                new ReportPlayerEntity(combatGroup.getOwnerSettlementId(), combatGroup.getOwnerNation(), combatGroup.getUnits(),
                        combatGroup.getUnits(), combatGroup.getPlunder(), 300),
                new ReportPlayerEntity(currentSettlement.getId(), currentSettlement.getNation(), currentSettlement.getHomeLegion(),
                        currentSettlement.getHomeLegion(), null, 0), LocalDateTime.now());
        var repo = settlementState.getReportRepository();
        repo.save(report);
        report.setReportOwner(currentSettlement.getAccountId());
        report.setId(null);
        repo.save(report);
    }
}
