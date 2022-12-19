package io.lanu.travian.game.services;

import io.lanu.travian.game.models.responses.ReportBriefResponse;
import io.lanu.travian.game.models.responses.ReportResponse;

import java.util.List;

public interface ReportService {
    List<ReportBriefResponse> getAllReportsBrief(String accountId);

    ReportResponse getById(String reportId);

    boolean readReports(List<String> reportsId);

    boolean deleteReports(List<String> reportsId);
}
