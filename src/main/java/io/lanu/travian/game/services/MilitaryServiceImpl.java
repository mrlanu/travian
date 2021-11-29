package io.lanu.travian.game.services;

import io.lanu.travian.enums.EManipulation;
import io.lanu.travian.enums.ENation;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.responses.MilitaryUnitResponse;
import io.lanu.travian.game.repositories.CombatUnitOrderRepository;
import io.lanu.travian.game.repositories.IMilitaryUnitRepository;
import io.lanu.travian.game.repositories.ResearchedCombatUnitRepository;
import io.lanu.travian.templates.military.CombatUnitFactory;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MilitaryServiceImpl implements MilitaryService {

    private final CombatUnitOrderRepository combatUnitOrderRepository;
    private final ResearchedCombatUnitRepository researchedCombatUnitRepository;
    private final VillageService villageService;
    private final IMilitaryUnitRepository militaryUnitRepository;

    public MilitaryServiceImpl(CombatUnitOrderRepository combatUnitOrderRepository,
                               ResearchedCombatUnitRepository researchedCombatUnitRepository,
                               VillageService villageService, IMilitaryUnitRepository militaryUnitRepository) {
        this.combatUnitOrderRepository = combatUnitOrderRepository;
        this.researchedCombatUnitRepository = researchedCombatUnitRepository;
        this.villageService = villageService;
        this.militaryUnitRepository = militaryUnitRepository;
    }

    @Override
    public List<MilitaryUnitResponse> getAllMilitaryUnitsByVillageId(String villageId) {
        var village = villageService.recalculateVillage(villageId);
        return List.of(new MilitaryUnitResponse("home-army", ENation.GALLS, false, villageId, village.getName(),
                new int[]{village.getX(), village.getY()}, villageId, CombatUnitFactory.mapHomeArmyToList(village.getHomeLegion()),
                null, village.calculateEatPerHour().intValue()));

    }

    @Override
    public List<ECombatUnit> getAllResearchedUnits(String villageId) {
        return researchedCombatUnitRepository.findByVillageId(villageId).getUnits()
                .stream()
                .map(shortUnit -> CombatUnitFactory.getUnit(shortUnit.getName(), shortUnit.getLevel()))
                .collect(Collectors.toList());
    }

    @Override
    public OrderCombatUnitEntity orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest) {

        VillageEntity villageEntity = villageService.recalculateVillage(orderCombatUnitRequest.getVillageId());
        ECombatUnit unit = orderCombatUnitRequest.getUnitType();
        List<OrderCombatUnitEntity> ordersList = getAllOrdersByVillageId(orderCombatUnitRequest.getVillageId());

        LocalDateTime lastTime = ordersList.size() > 0 ? ordersList.get(ordersList.size() - 1).getEndOrderTime() : LocalDateTime.now();

        LocalDateTime endOrderTime = lastTime.plus(
                orderCombatUnitRequest.getAmount() * unit.getTime(), ChronoUnit.SECONDS);

        OrderCombatUnitEntity armyOrder = new OrderCombatUnitEntity(orderCombatUnitRequest.getVillageId(), lastTime, orderCombatUnitRequest.getUnitType(),
                orderCombatUnitRequest.getAmount(), unit.getTime(), unit.getEat(), endOrderTime);

        spendResources(orderCombatUnitRequest.getAmount(), villageEntity, unit);

        villageService.saveVillage(villageEntity);
        return combatUnitOrderRepository.save(armyOrder);
    }

    private void spendResources(int unitsAmount, VillageEntity villageEntity, ECombatUnit kind) {
        Map<EResource, BigDecimal> neededResources = new HashMap<>();
        kind.getCost().forEach((k, v) -> neededResources.put(k, BigDecimal.valueOf((long) v * unitsAmount)));
        villageEntity.manipulateGoods(EManipulation.SUBTRACT, neededResources);
    }

    @Override
    public List<OrderCombatUnitEntity> getAllOrdersByVillageId(String villageId){
        return combatUnitOrderRepository
                .findAllByVillageId(villageId)
                .stream()
                .sorted(Comparator.comparing(OrderCombatUnitEntity::getCreated))
                .collect(Collectors.toList());
    }

}
