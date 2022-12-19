package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.enums.SettlementSubType;
import io.lanu.travian.enums.SettlementType;
import io.lanu.travian.game.entities.ResearchedCombatUnitEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.ResearchedCombatUnitShort;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.ShortVillageInfo;
import io.lanu.travian.game.models.responses.TileDetail;
import io.lanu.travian.game.models.responses.VillageView;
import io.lanu.travian.game.repositories.*;
import io.lanu.travian.security.UsersRepository;
import io.lanu.travian.templates.villages.VillageEntityFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SettlementServiceImpl implements SettlementService {
    private final SettlementRepository settlementRepository;
    private final MapTileRepository worldRepo;
    private final CombatGroupRepository combatGroupRepository;
    private final ResearchedCombatUnitRepository researchedCombatUnitRepository;
    private final ReportRepository reportRepository;

    public SettlementServiceImpl(SettlementRepository settlementRepository,
                                 MapTileRepository worldRepo,
                                 CombatGroupRepository combatGroupRepository, ResearchedCombatUnitRepository researchedCombatUnitRepository, UsersRepository usersRepository, ReportRepository reportRepository) {
        this.settlementRepository = settlementRepository;
        this.worldRepo = worldRepo;
        this.combatGroupRepository = combatGroupRepository;
        this.researchedCombatUnitRepository = researchedCombatUnitRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    public SettlementEntity findById(String villageId) {
        return settlementRepository.findById(villageId)
                .orElseThrow(() -> new IllegalStateException(String.format("Village with id - %s is not exist.", villageId)));
    }

    @Override
    public SettlementEntity newVillage(NewVillageRequest newVillageRequest) {
        var result = instantiateNewVillage(newVillageRequest);
        var availableTiles = worldRepo.getAllByEmptyTrue();
        var tile = availableTiles.get(getRandomBetween(0, availableTiles.size()));
        result.setX(tile.getCorX());
        result.setY(tile.getCorY());
        result.setId(tile.getId());
        result.setOwnerUserName(newVillageRequest.getOwnerUserName());
        tile.setClazz("village-galls");
        tile.setName(result.getName());
        tile.setOwnerId(result.getId());
        tile.setEmpty(false);
        worldRepo.save(tile);
        createResearchedCombatUnitEntity(result.getId());
        return saveVillage(result);
    }

    private int getRandomBetween(int min, int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }


    private SettlementEntity instantiateNewVillage(NewVillageRequest newVillageRequest){
        SettlementEntity newVillage = VillageEntityFactory.getVillageByType(SettlementType.VILLAGE, SettlementSubType.SIX);
        Objects.requireNonNull(newVillage).setAccountId(newVillageRequest.getAccountId());
        return newVillage;
    }

    private void createResearchedCombatUnitEntity(String villageId) {
        researchedCombatUnitRepository.save(
                new ResearchedCombatUnitEntity(villageId,
                        List.of(new ResearchedCombatUnitShort(ECombatUnit.PHALANX.getName(), 0))));
    }

    @Override
    public List<ShortVillageInfo> getAllVillagesByUserId(String userId) {
        return settlementRepository.findAllByAccountId(userId)
                .stream()
                .map(village -> new ShortVillageInfo(village.getId(), village.getName(), village.getX(), village.getY()))
                .collect(Collectors.toList());
    }

    @Override
    public SettlementEntity updateName(SettlementEntity settlementEntity, String newName) {
        settlementEntity.setName(newName);
        return settlementEntity;
    }

    @Override
    public Optional<SettlementEntity> findVillageByCoordinates(int x, int y) {
        return settlementRepository.findByXAndY(x, y);
    }

    @Override
    public SettlementEntity saveVillage(SettlementEntity settlementEntity){
        return settlementRepository.save(settlementEntity);
    }

    @Override
    public TileDetail getTileDetail(SettlementEntity settlement, int fromX, int fromY) {
        return new TileDetail(settlement.getId(), settlement.getSettlementType(), settlement.getSubType(),
                settlement.getNation(), settlement.getOwnerUserName(), settlement.getName(), settlement.getX(), settlement.getY(),
                settlement.getPopulation(), MilitaryServiceImpl.getDistance(settlement.getX(), settlement.getY(),
                fromX, fromY));
    }

    @Override
    public VillageView getVillageById(SettlementEntity settlementEntity) {
        var militariesInVillage =
                combatGroupRepository.getAllByToSettlementIdAndMoved(settlementEntity.getId(), false);
        return new VillageView(settlementEntity, settlementEntity.getConstructionEventList(),
                militariesInVillage);
    }

}
