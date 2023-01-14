package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatGroupMission;
import io.lanu.travian.enums.ENation;
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

import java.math.BigDecimal;
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
        boolean failed = false;
        var offSum = Arrays.stream(entity.getFrom().getTroops()).sum();
        var deadSum = Arrays.stream(entity.getFrom().getDead()).sum();
        if (accountId.equals(entity.getReportOwner()) && offSum == deadSum){
            failed = true;
        }

        var fromSettlement = settlementRepository.findById(entity.getFrom().getSettlementId()).orElseThrow();
        var toSettlement = settlementRepository.findById(entity.getTo().getSettlementId()).orElseThrow();

        var result = new ReportResponse(entity.getId(), entity.getReportOwner(), entity.getMission(),
                ReportPlayer.builder()
                        .settlementId(entity.getFrom().getSettlementId())
                        .settlementName(fromSettlement.getName())
                        .accountId(fromSettlement.getAccountId())
                        .playerName(fromSettlement.getOwnerUserName())
                        .nation(entity.getFrom().getNation())
                        .troops(entity.getFrom().getTroops())
                        .dead(entity.getFrom().getDead())
                        .bounty(entity.getFrom().getBounty())
                        .carry(entity.getFrom().getCarry())
                        .build(),
                ReportPlayer.builder()
                        .settlementId(entity.getTo().getSettlementId())
                        .settlementName(toSettlement.getName())
                        .accountId(toSettlement.getAccountId())
                        .playerName(toSettlement.getOwnerUserName())
                        .nation(entity.getTo().getNation())
                        .troops(failed ? new int[10] :entity.getTo().getTroops())
                        .dead(failed ? new int[10] : entity.getTo().getDead())
                        .bounty(entity.getTo().getBounty())
                        .carry(entity.getTo().getCarry())
                        .build(),
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
        if (cache.containsKey(reportEntity.getFrom().getSettlementId())) {
            from = cache.get(reportEntity.getFrom().getSettlementId());
        } else {
            from = settlementRepository.findById(reportEntity.getFrom().getSettlementId()).orElseThrow();
            cache.put(from.getId(), from);
        }
        if (cache.containsKey(reportEntity.getTo().getSettlementId())) {
            to = cache.get(reportEntity.getTo().getSettlementId());
        } else {
            to = settlementRepository.findById(reportEntity.getTo().getSettlementId()).orElseThrow();
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
