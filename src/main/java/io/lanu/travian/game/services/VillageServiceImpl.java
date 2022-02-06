package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.enums.EVillageType;
import io.lanu.travian.game.entities.ResearchedCombatUnitEntity;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.ResearchedCombatUnitShort;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.ShortVillageInfo;
import io.lanu.travian.game.repositories.*;
import io.lanu.travian.templates.villages.VillageEntityFactory;
import org.springframework.stereotype.Service;

import java.math.MathContext;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VillageServiceImpl implements VillageService{
    private final VillageRepository villageRepository;
    private final MapTileRepository worldRepo;
    private final ResearchedCombatUnitRepository researchedCombatUnitRepository;
    private static final MathContext mc = new MathContext(3);

    public VillageServiceImpl(VillageRepository villageRepository, MapTileRepository worldRepo,
                              ResearchedCombatUnitRepository researchedCombatUnitRepository) {
        this.villageRepository = villageRepository;
        this.worldRepo = worldRepo;
        this.researchedCombatUnitRepository = researchedCombatUnitRepository;
    }

    @Override
    public VillageEntity findById(String villageId) {
        return villageRepository.findById(villageId)
                .orElseThrow(() -> new IllegalStateException(String.format("Village with id - %s is not exist.", villageId)));
    }

    @Override
    public VillageEntity newVillage(NewVillageRequest newVillageRequest) {
        var result = villageRepository.save(instantiateNewVillage(newVillageRequest));
        var tile = worldRepo.getByCorXAndCorY(result.getX(), result.getY());
        tile.setClazz("village-galls");
        tile.setName(result.getName());
        worldRepo.save(tile);
        createResearchedCombatUnitEntity(result.getVillageId());
        return result;
    }

    private VillageEntity instantiateNewVillage(NewVillageRequest newVillageRequest){
        VillageEntity newVillage = VillageEntityFactory.getVillageByType(EVillageType.SIX);
        Objects.requireNonNull(newVillage).setAccountId(newVillageRequest.getAccountId());
        newVillage.setX(newVillageRequest.getX());
        newVillage.setY(newVillageRequest.getY());
        return newVillage;
    }

    private void createResearchedCombatUnitEntity(String villageId) {
        researchedCombatUnitRepository.save(
                new ResearchedCombatUnitEntity(villageId,
                        List.of(new ResearchedCombatUnitShort(ECombatUnit.PHALANX.getName(), 0))));
    }

    @Override
    public List<ShortVillageInfo> getAllVillagesByUserId(String userId) {
        return villageRepository.findAllByAccountId(userId)
                .stream()
                .map(village -> new ShortVillageInfo(village.getVillageId(), village.getName(), village.getX(), village.getY()))
                .collect(Collectors.toList());
    }

    @Override
    public VillageEntity updateName(VillageEntity villageEntity, String newName) {
        villageEntity.setName(newName);
        return villageEntity;
    }

    @Override
    public Optional<VillageEntity> findVillageByCoordinates(int x, int y) {
        return villageRepository.findByXAndY(x, y);
    }

    @Override
    public VillageEntity saveVillage(VillageEntity villageEntity){
        return villageRepository.save(villageEntity);
    }

}
