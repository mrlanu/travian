package io.lanu.travian.game.services;

import io.lanu.travian.game.models.responses.ReportBriefResponse;
import io.lanu.travian.game.models.responses.ReportResponse;

import java.util.List;

public interface ReportService {
    List<ReportBriefResponse> getAllReportsBrief(String settlementId);

    ReportResponse getById(String reportId);
}
