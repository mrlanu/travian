package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatGroupMission;
import io.lanu.travian.enums.SettlementType;
import io.lanu.travian.game.entities.ReportEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.ReportPlayer;
import io.lanu.travian.game.models.responses.ReportBriefResponse;
import io.lanu.travian.game.models.responses.ReportResponse;
import io.lanu.travian.game.repositories.ReportRepository;
import io.lanu.travian.game.repositories.SettlementRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService{

    private final ReportRepository reportRepository;
    private final SettlementRepository settlementRepository;

    public ReportServiceImpl(ReportRepository reportRepository, SettlementRepository settlementRepository) {
        this.reportRepository = reportRepository;
        this.settlementRepository = settlementRepository;
    }

    @Override
    public List<ReportBriefResponse> getAllReportsBrief(String accountId){
        var cache = new HashMap<String, SettlementEntity>();
        return reportRepository.findAllByReportOwner(accountId).stream()
                .sorted(Comparator.comparing(ReportEntity::getDateTime).reversed())
                .map(reportEntity -> buildBrief(cache, reportEntity)).collect(Collectors.toList());
    }

    @Override
    public ReportResponse getById(String reportId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accountId = authentication.getName();
        var entity = reportRepository.findById(reportId).orElseThrow();
        var offSettlement = settlementRepository
                .findById(entity.getOff().getSettlementId()).orElseThrow();
        boolean failed = false;
        var offSum = entity.getOff().getTroops().stream().reduce(0, Integer::sum);
        var deadSum = entity.getOff().getDead().stream().reduce(0, Integer::sum);
        if (accountId.equals(offSettlement.getAccountId()) && offSum.equals(deadSum)){
            failed = true;
        }

        boolean finalFailed = failed;
        var result = new ReportResponse(entity.getId(), entity.getReportOwner(), entity.getMission(),
                ReportPlayer.builder()
                        .settlementId(entity.getOff().getSettlementId())
                        .settlementName(offSettlement.getName())
                        .accountId(offSettlement.getAccountId())
                        .playerName(offSettlement.getOwnerUserName())
                        .nation(entity.getOff().getNation())
                        .troops(entity.getOff().getTroops())
                        .dead(entity.getOff().getDead())
                        .bounty(entity.getOff().getBounty())
                        .carry(entity.getOff().getCarry())
                        .build(),
                entity.getDef().stream().map(rE -> {
                    var settlement = settlementRepository
                            .findById(rE.getSettlementId()).orElseThrow();
                    return ReportPlayer.builder()
                        .settlementId(rE.getSettlementId())
                        .settlementName(settlement.getName())
                        .accountId(settlement.getAccountId())
                        .playerName(settlement.getOwnerUserName())
                        .nation(rE.getNation())
                        .troops(finalFailed ? Arrays.asList(0,0,0,0,0,0,0,0,0,0) : rE.getTroops())
                        .dead(finalFailed ? Arrays.asList(0,0,0,0,0,0,0,0,0,0) : rE.getDead())
                        .bounty(rE.getBounty())
                        .carry(rE.getCarry())
                        .build();}).collect(Collectors.toList()),
                /*ReportPlayer.builder()
                        .settlementId(entity.getDef().get(0).getSettlementId())
                        .settlementName(toSettlement.getName())
                        .accountId(toSettlement.getAccountId())
                        .playerName(toSettlement.getOwnerUserName())
                        .nation(entity.getDef().get(0).getNation())
                        .troops(failed ? Arrays.asList(0,0,0,0,0,0,0,0,0,0) :entity.getDef().get(0).getTroops())
                        .dead(failed ? Arrays.asList(0,0,0,0,0,0,0,0,0,0) : entity.getDef().get(0).getDead())
                        .bounty(entity.getDef().get(0).getBounty())
                        .carry(entity.getDef().get(0).getCarry())
                        .build(),*/
                entity.getDateTime(), entity.isRead(), failed);
        return result;
    }

    @Override
    public boolean readReports(List<String> reportsId) {
        var reports = reportRepository.findAllById(reportsId);
        reports.forEach(reportEntity -> reportEntity.setRead(true));
        reportRepository.saveAll(reports);
        return true;
    }

    @Override
    public boolean deleteReports(List<String> reportsId) {
        reportRepository.deleteAllById(reportsId);
        return true;
    }

    @Override
    public long countAllByReportOwnerIdAndRead(String ownerId) {
        return reportRepository.countAllByReportOwnerAndRead(ownerId, false);
    }

    private ReportBriefResponse buildBrief(Map<String, SettlementEntity> cache, ReportEntity reportEntity){
        SettlementEntity from;
        SettlementEntity to;
        if (cache.containsKey(reportEntity.getOff().getSettlementId())) {
            from = cache.get(reportEntity.getOff().getSettlementId());
        } else {
            from = settlementRepository.findById(reportEntity.getOff().getSettlementId()).orElseThrow();
            cache.put(from.getId(), from);
        }
        if (cache.containsKey(reportEntity.getDef().get(0).getSettlementId())) {
            to = cache.get(reportEntity.getDef().get(0).getSettlementId());
        } else {
            to = settlementRepository.findById(reportEntity.getDef().get(0).getSettlementId()).orElseThrow();
            cache.put(to.getId(), to);
        }
        var briefSubject = new StringBuilder();
        String mission;
        if (reportEntity.getMission().equals(ECombatGroupMission.REINFORCEMENT)){
            mission = "reinforces";
        }else if (reportEntity.getMission().equals(ECombatGroupMission.ATTACK)){
            mission = "attacks";
        }else {
            mission = "raids";
        }
        briefSubject
                .append(from.getName())
                .append(" ")
                .append(mission)
                .append(" ")
                .append(to.getName());
        if (to.getSettlementType().equals(SettlementType.OASIS)){
            briefSubject.append(" ").append("(").append(to.getX()).append("|").append(to.getY()).append(")");
        }

        return new ReportBriefResponse(reportEntity.getId(), reportEntity.isRead(),
                briefSubject.toString(), reportEntity.getDateTime());
    }
}
