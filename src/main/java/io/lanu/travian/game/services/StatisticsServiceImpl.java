package io.lanu.travian.game.services;

import io.lanu.travian.Consts;
import io.lanu.travian.game.entities.StatisticsEntity;
import io.lanu.travian.game.models.responses.StatisticsResponse;
import io.lanu.travian.game.repositories.StatisticsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class StatisticsServiceImpl implements StatisticsService{

    private final StatisticsRepository statisticsRepository;

    public StatisticsServiceImpl(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    @Override
    public StatisticsEntity save(StatisticsEntity entity) {
        return statisticsRepository.save(entity);
    }

    @Override
    public Map<String, Object> getStatistics(String statisticsId, Integer page, String sortBy){
        var allPlayers = statisticsRepository
                .findAll(Sort.by(sortBy).descending());
        if (statisticsId == null){
            return getStatisticsByPageNumber(allPlayers, page);
        }else {
            return getStatisticsPageWithUser(allPlayers, statisticsId);
        }
    }

    private Map<String, Object> getStatisticsByPageNumber(List<StatisticsEntity> allPlayers, Integer pageNumber) {
        var mapper = new ModelMapper();
        List<StatisticsResponse> responseList = new ArrayList<>();
        var start = pageNumber * Consts.STATISTICS_SIZE - Consts.STATISTICS_SIZE + 1;
        IntStream.range(start, start + Consts.STATISTICS_SIZE)
                .forEach(idx -> {
                    if (idx < allPlayers.size() + 1){
                        var mapped = mapper.map(allPlayers.get(idx - 1), StatisticsResponse.class);
                        mapped.setIndex(idx);
                        responseList.add(mapped);
                    }
                });
        return getResponse(allPlayers.size(), responseList, pageNumber);
    }

    private Map<String, Object> getStatisticsPageWithUser(List<StatisticsEntity> allPlayers, String statisticsId){
        var mapper = new ModelMapper();

        List<StatisticsResponse> responseList = new ArrayList<>();
        OptionalInt index = allPlayers.stream()
                .filter(s -> s.getId().equals(statisticsId))
                .mapToInt(allPlayers::indexOf)
                .findFirst();

        var playerOnPage = ((index.orElseThrow() + 1) / Consts.STATISTICS_SIZE);
        if (((index.getAsInt() + 1) % Consts.STATISTICS_SIZE) != 0){
            playerOnPage += 1;
        }

        IntStream.range((playerOnPage - 1) * Consts.STATISTICS_SIZE + 1, playerOnPage * Consts.STATISTICS_SIZE + 1)
                .forEach(idx -> {
                    if (idx < allPlayers.size() + 1){
                        var mapped = mapper.map(allPlayers.get(idx - 1), StatisticsResponse.class);
                        mapped.setIndex(idx);
                        responseList.add(mapped);
                    }
                });
        /*Pageable pageable = PageRequest.of(playerOnPage, Consts.STATISTICS_SIZE);
        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), responseList.size());
        var test = new PageImpl<>(responseList.subList(start, end), pageable, responseList.size());*/
        return getResponse(allPlayers.size(), responseList, playerOnPage);
    }

    private static Map<String, Object> getResponse(int allPlayersAmount, List<StatisticsResponse> responseList, Integer currentPage) {
        var totalPages = allPlayersAmount / Consts.STATISTICS_SIZE;
        Map<String, Object> response = new HashMap<>();
        response.put("statistics", responseList);
        response.put("currentPage", currentPage);
        response.put("totalItems", allPlayersAmount);
        response.put("totalPages", allPlayersAmount % Consts.STATISTICS_SIZE == 0 ? totalPages : totalPages + 1);
        response.put("itemsPerPage", Consts.STATISTICS_SIZE);
        return response;
    }
}
