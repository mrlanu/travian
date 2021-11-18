package io.lanu.travian.game.services;

import io.lanu.travian.enums.ENations;
import io.lanu.travian.enums.EUnits;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.entities.ArmyOrderEntity;
import io.lanu.travian.game.entities.ResearchedUnitsEntity;
import io.lanu.travian.game.entities.events.TroopBuildEvent;
import io.lanu.travian.game.models.ResearchedUnitShort;
import io.lanu.travian.game.models.TroopUnit;
import io.lanu.travian.game.models.requests.ArmyOrderRequest;
import io.lanu.travian.game.repositories.ArmyOrdersRepository;
import io.lanu.travian.game.repositories.ResearchedUnitsRepository;
import io.lanu.travian.templates.military.MilitaryUnitsFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MilitaryServiceImpl implements MilitaryService {

    private final ArmyOrdersRepository armyOrdersRepository;
    private final ResearchedUnitsRepository researchedUnitsRepository;

    public MilitaryServiceImpl(ArmyOrdersRepository armyOrdersRepository, ResearchedUnitsRepository researchedUnitsRepository) {
        this.armyOrdersRepository = armyOrdersRepository;
        this.researchedUnitsRepository = researchedUnitsRepository;
    }

    @Override
    public void createResearchedUnits(String villageId) {
        researchedUnitsRepository.save(new ResearchedUnitsEntity(villageId, Arrays.asList(new ResearchedUnitShort(EUnits.PHALANX.getName(), 0))));
    }

    @Override
    public List<EUnits> getAllResearchedUnits(String villageId) {
        return researchedUnitsRepository.findByVillageId(villageId).getUnits()
                .stream()
                .map(shortUnit -> MilitaryUnitsFactory.getUnit(shortUnit.getName(), shortUnit.getLevel()))
                .collect(Collectors.toList());
    }

    @Override
    public ArmyOrderEntity orderUnits(ArmyOrderRequest armyOrderRequest) {
        TroopUnit troop = new TroopUnit(EUnits.PHALANX, ENations.ROME,
                Map.of(
                    EResource.WOOD, BigDecimal.valueOf(120),
                    EResource.CLAY, BigDecimal.valueOf(100),
                    EResource.IRON, BigDecimal.valueOf(150),
                    EResource.CROP, BigDecimal.valueOf(30)),
                40, 35, 60, 6, 50, 1, 10);

        List<ArmyOrderEntity> ordersList = getAllOrdersByVillageId(armyOrderRequest.getVillageId());

        LocalDateTime lastTime = ordersList.size() > 0 ? ordersList.get(ordersList.size() - 1).getEndOrderTime() : LocalDateTime.now();

        LocalDateTime endOrderTime = lastTime.plus(
                armyOrderRequest.getAmount() * troop.getBaseProductionTime(), ChronoUnit.SECONDS);

        ArmyOrderEntity armyOrder = new ArmyOrderEntity(armyOrderRequest.getVillageId(), lastTime, armyOrderRequest.getUnitType(),
                armyOrderRequest.getAmount(), troop.getBaseProductionTime(), troop.getEatHour(), endOrderTime);

        return armyOrdersRepository.save(armyOrder);
    }

    @Override
    public List<ArmyOrderEntity> getAllOrdersByVillageId(String villageId){
        return armyOrdersRepository
                .findAllByVillageId(villageId)
                .stream()
                .sorted(Comparator.comparing(ArmyOrderEntity::getCreated))
                .collect(Collectors.toList());
    }

    @Override
    public List<TroopBuildEvent> createTroopsBuildEventsFromOrders(String villageId) {

        List<TroopBuildEvent> result = new ArrayList<>();
        List<ArmyOrderEntity> ordersList = getAllOrdersByVillageId(villageId);

        if (ordersList.size() > 0) {
            for (ArmyOrderEntity order : ordersList) {
                long duration = Duration.between(order.getLastTime(), LocalDateTime.now()).toSeconds();

                if (LocalDateTime.now().isAfter(order.getEndOrderTime())) {
                    // add all troops from order to result list
                    result.addAll(addCompletedTroops(order, order.getLeftTrain()));
                    armyOrdersRepository.deleteById(order.getOrderId());
                    continue;
                }

                int completedTroops = (int) (duration / order.getDurationEach());

                if (completedTroops > 0) {
                    // add completed troops from order to result list
                    result.addAll(addCompletedTroops(order, completedTroops));
                    order.setLeftTrain(order.getLeftTrain() - completedTroops);
                    order.setLastTime(order.getLastTime().plus(completedTroops * order.getDurationEach(), ChronoUnit.SECONDS));
                    armyOrdersRepository.save(order);
                }
            }
        }
        return result;
    }

    private List<TroopBuildEvent> addCompletedTroops(ArmyOrderEntity order, Integer amount) {
        List<TroopBuildEvent> result = new ArrayList<>();
        LocalDateTime exec = order.getLastTime();
        for (int i = 0; i < amount; i++) {
            exec = exec.plus(order.getDurationEach(), ChronoUnit.SECONDS);
            result.add(new TroopBuildEvent(exec, order.getUnitType(), order.getEatHour()));
        }
        return result;
    }

}
