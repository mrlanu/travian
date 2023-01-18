package io.lanu.travian.game.services;

import io.lanu.travian.enums.SettlementSubType;
import io.lanu.travian.enums.SettlementType;
import io.lanu.travian.game.dto.SettlementStateDTO;
import io.lanu.travian.game.entities.MapTile;
import io.lanu.travian.game.entities.ResearchedCombatUnitEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.ResearchedCombatUnitShort;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.SettlementView;
import io.lanu.travian.game.models.responses.ShortVillageInfo;
import io.lanu.travian.game.models.responses.TileDetail;
import io.lanu.travian.game.repositories.MapTileRepository;
import io.lanu.travian.game.repositories.ResearchedCombatUnitRepository;
import io.lanu.travian.game.repositories.SettlementRepository;
import io.lanu.travian.game.repositories.StatisticsRepository;
import io.lanu.travian.templates.villages.VillageEntityFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SettlementServiceImpl implements SettlementService {

    private final EngineService engineService;
    private final SettlementRepository settlementRepository;
    private final MapTileRepository worldRepo;
    private final ResearchedCombatUnitRepository researchedCombatUnitRepository;
    private final StatisticsRepository statisticsRepository;

    public SettlementServiceImpl(
            EngineService engineService, SettlementRepository settlementRepository,
            MapTileRepository worldRepo,
            ResearchedCombatUnitRepository researchedCombatUnitRepository, StatisticsRepository statisticsRepository) {
        this.engineService = engineService;
        this.settlementRepository = settlementRepository;
        this.worldRepo = worldRepo;
        this.researchedCombatUnitRepository = researchedCombatUnitRepository;
        this.statisticsRepository = statisticsRepository;
    }

    @Override
    public Optional<SettlementEntity> findById(String villageId) {
        return settlementRepository.findById(villageId);
                //.orElseThrow(() -> new IllegalStateException(String.format("Village with id - %s is not exist.", villageId)));
    }

    @Override
    public SettlementStateDTO newVillage(NewVillageRequest newVillageRequest) {
        var state = instantiateNewVillage(newVillageRequest);
        MapTile tile;
        if (newVillageRequest.getX().equals(0) && newVillageRequest.getY().equals(0)){
            var availableTiles = worldRepo.getAllByEmptyTrue();
            tile = availableTiles.get(getRandomBetween(0, availableTiles.size()));
        }else {
            tile = worldRepo.getByCorXAndCorY(newVillageRequest.getX(), newVillageRequest.getY());
        }
        state.getSettlementEntity().setX(tile.getCorX());
        state.getSettlementEntity().setY(tile.getCorY());
        state.getSettlementEntity().setId(tile.getId());
        state.getSettlementEntity().setOwnerUserName(newVillageRequest.getOwnerUserName());
        tile.setClazz("village-galls");
        tile.setName(state.getSettlementEntity().getName());
        tile.setOwnerId(state.getSettlementEntity().getId());
        tile.setEmpty(false);
        worldRepo.save(tile);
        createResearchedCombatUnitEntity(state.getSettlementEntity().getId());
        editStatistics(newVillageRequest.getAccountId());
        return engineService.saveSettlementEntity(state);
    }

    private void editStatistics(String accountId) {
        var statistics = statisticsRepository.findByPlayerId(accountId);
        statistics.addVillage(1);
        statisticsRepository.save(statistics);
    }

    private int getRandomBetween(int min, int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }


    private SettlementStateDTO instantiateNewVillage(NewVillageRequest newVillageRequest){
        var state = SettlementStateDTO.builder()
                .settlementEntity(VillageEntityFactory.getVillageByType(SettlementType.VILLAGE, SettlementSubType.SIX))
                .build();
        Objects.requireNonNull(state.getSettlementEntity()).setAccountId(newVillageRequest.getAccountId());
        return state;
    }

    private void createResearchedCombatUnitEntity(String villageId) {
        researchedCombatUnitRepository.save(
                new ResearchedCombatUnitEntity(villageId,
                        List.of(new ResearchedCombatUnitShort(0, 0))));
    }

    @Override
    public List<ShortVillageInfo> getAllVillagesByUserId(String userId) {
        return settlementRepository.findAllByAccountId(userId)
                .stream()
                .map(village -> new ShortVillageInfo(village.getId(), village.getName(), village.getX(), village.getY()))
                .collect(Collectors.toList());
    }

    @Override
    public SettlementStateDTO updateName(String settlementId, String newName) {
        var currentState = engineService.updateParticularSettlementState(settlementId, LocalDateTime.now());
        currentState.getSettlementEntity().setName(newName);
        return engineService.saveSettlementEntity(currentState);
    }

    @Override
    public Optional<SettlementEntity> findVillageByCoordinates(int x, int y) {
        return settlementRepository.findByXAndY(x, y);
    }

    @Override
    public SettlementStateDTO saveSettlement(SettlementStateDTO settlementState){
        return engineService.saveSettlementEntity(settlementState);
    }

    @Override
    public TileDetail getTileDetail(SettlementEntity settlement, int fromX, int fromY) {
        return new TileDetail(settlement.getId(), settlement.getSettlementType(), settlement.getSubType(),
                settlement.getNation(), settlement.getOwnerUserName(), settlement.getName(), settlement.getX(), settlement.getY(),
                settlement.getPopulation(), MilitaryServiceImpl.getDistance(settlement.getX(), settlement.getY(),
                fromX, fromY));
    }

    @Override
    public SettlementView getSettlementById(String settlementId) {
        var currentState = engineService.updateAccount(settlementId);
        return new SettlementView(currentState);
    }

    @Override
    public SettlementView getSettlementById(SettlementStateDTO currentState) {
        var updatedState = engineService.updateAccount(currentState.getSettlementEntity().getId());
        return new SettlementView(updatedState);
    }

}
