package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.enums.EVillageType;
import io.lanu.travian.game.entities.ResearchedCombatUnitEntity;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.*;
import io.lanu.travian.game.models.ResearchedCombatUnitShort;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.ShortVillageInfo;
import io.lanu.travian.game.models.responses.VillageView;
import io.lanu.travian.game.repositories.ResearchedCombatUnitRepository;
import io.lanu.travian.game.repositories.VillageRepository;
import io.lanu.travian.templates.villages.VillageEntityFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VillageServiceImpl implements VillageService{
    private final VillageRepository villageRepository;
    private final ResearchedCombatUnitRepository researchedCombatUnitRepository;
    private final IConstructionService constructionService;
    private static final MathContext mc = new MathContext(3);

    public VillageServiceImpl(VillageRepository villageRepository, ResearchedCombatUnitRepository researchedCombatUnitRepository,
                              IConstructionService constructionService) {
        this.villageRepository = villageRepository;
        this.researchedCombatUnitRepository = researchedCombatUnitRepository;
        this.constructionService = constructionService;
    }

    @Override
    public VillageEntity findById(String villageId) {
        return villageRepository.findById(villageId)
                .orElseThrow(() -> new IllegalStateException(String.format("Village with id - %s is not exist.", villageId)));
    }

    @Override
    public VillageEntity createVillage(NewVillageRequest newVillageRequest) {
        var result = villageRepository.save(instantiateNewVillage(newVillageRequest));
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
    public String updateName(String villageId, String newName) {
        VillageEntity villageEntity = this.villageRepository.findById(villageId)
                .orElseThrow(() -> new IllegalStateException(String.format("Village with id - %s is not exist.", villageId)));
        villageEntity.setName(newName);
        villageRepository.save(villageEntity);
        return newName;
    }

    @Override
    public Optional<VillageEntity> findVillageByCoordinates(int x, int y) {
        return villageRepository.findByXAndY(x, y);
    }

    @Override
    public VillageEntity saveVillage(VillageEntity villageEntity){
        return villageRepository.save(villageEntity);
    }

    @Override
    public VillageView getVillageById(VillageEntity villageEntity) {
        List<ConstructionEvent> currentBuildingEvents = constructionService.findAllByVillageId(villageEntity.getVillageId());
        return new VillageView(villageEntity, currentBuildingEvents);
    }

}
