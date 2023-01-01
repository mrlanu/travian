package io.lanu.travian.game.controllers;

import io.lanu.travian.game.services.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping()
    public Map<String, Object> findAll(@RequestParam(defaultValue = "population") String sort,
                                       @RequestParam(required = false) String statisticsId,
                                       @RequestParam(required = false) Integer page){
        return statisticsService.getStatistics(statisticsId, page, sort);
    }
}
