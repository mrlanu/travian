package io.lanu.travian.game.services;

import io.lanu.travian.enums.EManipulation;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.enums.EUnits;
import io.lanu.travian.game.entities.ArmyOrderEntity;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.requests.ArmyOrderRequest;
import io.lanu.travian.game.repositories.ArmyOrdersRepository;
import io.lanu.travian.game.repositories.ResearchedTroopsRepository;
import io.lanu.travian.templates.military.MilitaryUnitsFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MilitaryServiceImpl implements MilitaryService {

    private final ArmyOrdersRepository armyOrdersRepository;
    private final ResearchedTroopsRepository researchedTroopsRepository;
    private final VillageService villageService;

    public MilitaryServiceImpl(ArmyOrdersRepository armyOrdersRepository,
                               ResearchedTroopsRepository researchedTroopsRepository,
                               VillageService villageService) {
        this.armyOrdersRepository = armyOrdersRepository;
        this.researchedTroopsRepository = researchedTroopsRepository;
        this.villageService = villageService;
    }

    @Override
    public List<EUnits> getAllResearchedUnits(String villageId) {
        return researchedTroopsRepository.findByVillageId(villageId).getUnits()
                .stream()
                .map(shortUnit -> MilitaryUnitsFactory.getUnit(shortUnit.getName(), shortUnit.getLevel()))
                .collect(Collectors.toList());
    }

    @Override
    public ArmyOrderEntity orderUnits(ArmyOrderRequest armyOrderRequest) {

        VillageEntity villageEntity = villageService.recalculateVillage(armyOrderRequest.getVillageId());
        EUnits unit = armyOrderRequest.getUnitType();
        List<ArmyOrderEntity> ordersList = getAllOrdersByVillageId(armyOrderRequest.getVillageId());

        LocalDateTime lastTime = ordersList.size() > 0 ? ordersList.get(ordersList.size() - 1).getEndOrderTime() : LocalDateTime.now();

        LocalDateTime endOrderTime = lastTime.plus(
                armyOrderRequest.getAmount() * unit.getTime(), ChronoUnit.SECONDS);

        ArmyOrderEntity armyOrder = new ArmyOrderEntity(armyOrderRequest.getVillageId(), lastTime, armyOrderRequest.getUnitType(),
                armyOrderRequest.getAmount(), unit.getTime(), unit.getEat(), endOrderTime);

        spendResources(armyOrderRequest.getAmount(), villageEntity, unit);

        villageService.saveVillage(villageEntity);
        return armyOrdersRepository.save(armyOrder);
    }

    private void spendResources(int unitsAmount, VillageEntity villageEntity, EUnits kind) {
        Map<EResource, BigDecimal> neededResources = new HashMap<>();
        kind.getCost().forEach((k, v) -> neededResources.put(k, BigDecimal.valueOf((long) v * unitsAmount)));
        villageEntity.manipulateGoods(EManipulation.SUBTRACT, neededResources);
    }

    @Override
    public List<ArmyOrderEntity> getAllOrdersByVillageId(String villageId){
        return armyOrdersRepository
                .findAllByVillageId(villageId)
                .stream()
                .sorted(Comparator.comparing(ArmyOrderEntity::getCreated))
                .collect(Collectors.toList());
    }

}
