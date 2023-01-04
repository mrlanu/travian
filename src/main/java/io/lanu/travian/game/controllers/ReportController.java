package io.lanu.travian.game.controllers;

import io.lanu.travian.game.models.responses.ReportBriefResponse;
import io.lanu.travian.game.models.responses.ReportResponse;
import io.lanu.travian.game.services.ReportService;
import io.lanu.travian.game.services.EngineService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReportController {

    private final EngineService state;
    private final ReportService reportService;

    public ReportController(EngineService state, ReportService reportService) {
        this.state = state;
        this.reportService = reportService;
    }

    @GetMapping("/reports/count-new")
    public long countNewReports(@RequestParam String accountId){
        return reportService.countAllByReportOwnerIdAndRead(accountId);
    }

    @GetMapping("/reports")
    public List<ReportBriefResponse> getAllReportsBrief(@RequestParam String accountId, @RequestParam String settlementId){
        state.updateParticularSettlementState(settlementId, LocalDateTime.now());
        return reportService.getAllReportsBrief(accountId);
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
