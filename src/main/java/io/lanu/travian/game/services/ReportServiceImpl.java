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
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        var entity = reportRepository.findById(reportId).orElseThrow();
        var fromSettlement = settlementRepository.findById(entity.getFrom().getSettlementId()).orElseThrow();
        var toSettlement = settlementRepository.findById(entity.getTo().getSettlementId()).orElseThrow();

        var result = new ReportResponse(entity.getId(), entity.getReportOwner(), entity.getMission(),
                new ReportPlayer(entity.getFrom().getSettlementId(), fromSettlement.getName(), fromSettlement.getAccountId(),
                        fromSettlement.getOwnerUserName(), entity.getFrom().getNation(), entity.getFrom().getTroops(), entity.getFrom().getDead(),
                        entity.getFrom().getBounty(), entity.getFrom().getCarry()),
                new ReportPlayer(entity.getTo().getSettlementId(), toSettlement.getName(), toSettlement.getAccountId(),
                        toSettlement.getOwnerUserName(), entity.getTo().getNation(), entity.getTo().getTroops(), entity.getTo().getDead(),
                        entity.getTo().getBounty(), entity.getTo().getCarry()),
                entity.getDateTime(), entity.isRead());
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
