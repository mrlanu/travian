package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatGroupMission;
import io.lanu.travian.enums.SettlementType;
import io.lanu.travian.game.entities.ReportEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.responses.ReportBriefResponse;
import io.lanu.travian.game.models.responses.ReportResponse;
import io.lanu.travian.game.repositories.ReportRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
    public List<ReportBriefResponse> getAllReportsBrief(String settlementId){
        var cache = new HashMap<String, SettlementEntity>();
        return reportRepository.findAllByReportOwner(settlementId).stream()
                .map(reportEntity -> buildBrief(cache, reportEntity)).collect(Collectors.toList());
    }

    @Override
    public ReportResponse getById(String reportId) {
        var mapper = new ModelMapper();
        var entity = reportRepository.findById(reportId).orElseThrow();
        return mapper.map(entity, ReportResponse.class);
    }

    private ReportBriefResponse buildBrief(Map<String, SettlementEntity> cache, ReportEntity reportEntity){
        SettlementEntity from;
        SettlementEntity to;
        if (cache.containsKey(reportEntity.getFrom().getSettlementId())) {
            from = cache.get(reportEntity.getFrom().getSettlementId());
        } else {
            from = settlementRepository.findById(reportEntity.getFrom().getSettlementId());
            cache.put(from.getId(), from);
        }
        if (cache.containsKey(reportEntity.getTo().getSettlementId())) {
            to = cache.get(reportEntity.getTo().getSettlementId());
        } else {
            to = settlementRepository.findById(reportEntity.getTo().getSettlementId());
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
