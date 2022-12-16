package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.StatisticsEntity;

import java.util.Map;

public interface StatisticsService {
    StatisticsEntity save(StatisticsEntity entity);
    Map<String, Object> getStatistics(String statisticsId, Integer page, String sortBy);
}
