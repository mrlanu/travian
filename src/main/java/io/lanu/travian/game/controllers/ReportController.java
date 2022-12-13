package io.lanu.travian.game.controllers;

import io.lanu.travian.game.models.responses.ReportBriefResponse;
import io.lanu.travian.game.models.responses.ReportResponse;
import io.lanu.travian.game.services.ReportService;
import io.lanu.travian.game.services.SettlementState;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villages")
public class ReportController {

    private final SettlementState state;
    private final ReportService reportService;

    public ReportController(SettlementState state, ReportService reportService) {
        this.state = state;
        this.reportService = reportService;
    }

    @GetMapping("/{villageId}/reports")
    public List<ReportBriefResponse> getAllReportsBrief(@PathVariable String villageId){
        state.recalculateCurrentState(villageId);
        var reports = reportService.getAllReportsBrief(villageId);
        return reports;
    }

    @GetMapping("/reports/{reportId}")
    public ReportResponse getReportById(@PathVariable String reportId){
        return reportService.getById(reportId);
    }

    @PutMapping("/reports/read")
    public boolean readReports(@RequestBody List<String> reportsId){
        return reportService.readReports(reportsId);
    }

    @PutMapping("/reports/delete")
    public boolean deleteReports(@RequestBody List<String> reportsId){
        return reportService.deleteReports(reportsId);
    }
}
