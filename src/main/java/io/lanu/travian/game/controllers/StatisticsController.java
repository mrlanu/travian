package io.lanu.travian.game.controllers;

import io.lanu.travian.game.entities.StatisticsEntity;
import io.lanu.travian.game.services.StatisticsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @PostMapping()
    public StatisticsEntity add(@RequestBody StatisticsEntity statisticsEntity){
        return statisticsService.save(statisticsEntity);
    }

    @GetMapping()
    public Map<String, Object> findAll(@RequestParam(defaultValue = "population") String sort,
                                       @RequestParam(required = false) String statisticsId,
                                       @RequestParam(required = false) Integer page){
        return statisticsService.getStatistics(statisticsId, page, sort);
    }
}
