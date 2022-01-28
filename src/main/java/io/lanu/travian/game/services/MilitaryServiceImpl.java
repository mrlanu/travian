package io.lanu.travian.game.services;

import io.lanu.travian.enums.*;
import io.lanu.travian.game.entities.events.CombatUnitDoneEvent;
import io.lanu.travian.game.entities.events.MilitaryUnit;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.MilitaryUnitDynamic;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.*;
import io.lanu.travian.game.repositories.CombatUnitOrderRepository;
import io.lanu.travian.game.repositories.IMilitaryUnitRepository;
import io.lanu.travian.game.repositories.ResearchedCombatUnitRepository;
import io.lanu.travian.security.UsersRepository;
import io.lanu.travian.templates.military.CombatUnitFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MilitaryServiceImpl implements MilitaryService {

    private final CombatUnitOrderRepository combatUnitOrderRepository;
    private final ResearchedCombatUnitRepository researchedCombatUnitRepository;
    private final IMilitaryUnitRepository militaryUnitRepository;
    private final UsersRepository usersRepository;

    public MilitaryServiceImpl(CombatUnitOrderRepository combatUnitOrderRepository,
                               ResearchedCombatUnitRepository researchedCombatUnitRepository,
                               IMilitaryUnitRepository militaryUnitRepository, UsersRepository usersRepository) {
        this.combatUnitOrderRepository = combatUnitOrderRepository;
        this.researchedCombatUnitRepository = researchedCombatUnitRepository;
        this.militaryUnitRepository = militaryUnitRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public List<MilitaryUnit> getAllByOriginVillageId(String villageId) {
        return militaryUnitRepository.getAllByOriginVillageId(villageId);
    }

    @Override
    public Map<String, List<MilitaryUnitView>> getAllMilitaryUnitsByVillage(VillageEntity village) {
        /*var userName = usersRepository.findByUserId(village.getAccountId()).orElseThrow();
        var villageId = village.getVillageId();
        // other units
        List<MilitaryUnitView> unitsList = militaryUnitRepository.getAllByOriginVillageIdOrTargetVillageId(villageId, villageId)
                .stream()
                .peek(militaryUnit -> {
                    if (militaryUnit.getOriginVillageId().equals(villageId)){
                        militaryUnit.setState(EMilitaryUnitState.OUT);
                    }else {
                        militaryUnit.setState(EMilitaryUnitState.IN);
                    }
                })
                .map(mEv -> new MilitaryUnitViewDynamic(mEv.getId(), mEv.getNation(), true, mEv.getState(),
                                new VillageBrief(mEv.getOriginVillageId(), mEv.getOriginVillageName(), mEv.getOriginPlayerName(), mEv.getOriginVillageCoordinates()),
                                mEv.getUnits(), mEv.getMission(),
                                new VillageBrief(mEv.getTargetVillageId(), mEv.getTargetVillageName(), mEv.getTargetPlayerName(), mEv.getTargetVillageCoordinates()),
                                mEv.getExecutionTime(), mEv.getDuration()))
                .collect(Collectors.toList());

        Map<String, List<MilitaryUnitView>> militaryUnitsMap = unitsList.stream()
                .collect(Collectors.groupingBy(militaryEvent -> militaryEvent.getState().getName()));

        // home army
        MilitaryUnitView homeArmy = new MilitaryUnitViewStatic("home", village.getNation(), false, EMilitaryUnitState.HOME,
                new VillageBrief(villageId, village.getName(), userName.getUsername(), new int[]{village.getX(), village.getY()}),
                village.getHomeLegion(), villageId, 5);

        militaryUnitsMap.put(EMilitaryUnitState.HOME.getName(), List.of(homeArmy));
        return militaryUnitsMap;*/
        return null;
    }

    @Override
    public List<ECombatUnit> getAllResearchedUnits(String villageId) {
        return researchedCombatUnitRepository.findByVillageId(villageId).getUnits()
                .stream()
                .map(shortUnit -> CombatUnitFactory.getUnit(shortUnit.getName(), shortUnit.getLevel()))
                .collect(Collectors.toList());
    }

    @Override
    public VillageEntity orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest, VillageEntity village) {

        ECombatUnit unit = orderCombatUnitRequest.getUnitType();
        List<OrderCombatUnitEntity> ordersList = getAllOrdersByVillageId(orderCombatUnitRequest.getVillageId());

        LocalDateTime lastTime = ordersList.size() > 0 ? ordersList.get(ordersList.size() - 1).getEndOrderTime() : LocalDateTime.now();

        LocalDateTime endOrderTime = lastTime.plus(
                orderCombatUnitRequest.getAmount() * unit.getTime(), ChronoUnit.SECONDS);

        OrderCombatUnitEntity armyOrder = new OrderCombatUnitEntity(orderCombatUnitRequest.getVillageId(), lastTime, orderCombatUnitRequest.getUnitType(),
                orderCombatUnitRequest.getAmount(), unit.getTime(), unit.getEat(), endOrderTime);

        spendResources(orderCombatUnitRequest.getAmount(), village, unit);

        combatUnitOrderRepository.save(armyOrder);
        return village;
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

    @Override
    public MilitaryUnitContract checkTroopsSendingRequest(TroopsSendingRequest troopsSendingRequest, VillageEntity attackingVillage, VillageEntity attackedVillage) {
        var attackingUser = usersRepository.findByUserId(attackingVillage.getAccountId()).orElseThrow();
        var attackedUser = usersRepository.findByUserId(attackedVillage.getAccountId()).orElseThrow();
        return MilitaryUnitContract.builder()
                .nation(attackingVillage.getNation())
                .mission(troopsSendingRequest.getKind().getName())
                .originVillageId(attackingVillage.getVillageId())
                .originVillageName(attackingVillage.getName())
                .originPlayerName(attackingUser.getUsername())
                .originVillageCoordinates(new int[]{attackingVillage.getX(), attackingVillage.getY()})
                .targetVillageId(attackedVillage.getVillageId())
                .targetVillageName(attackedVillage.getName())
                .targetPlayerName(attackedUser.getUsername())
                .targetVillageCoordinates(new int[]{attackedVillage.getX(), attackedVillage.getY()})
                .units(troopsSendingRequest.getWaves().get(0).getTroops())
                .arrivalTime(LocalDateTime.now().plusMinutes(2))
                .duration(120)
                .build();
    }

    @Override
    public VillageEntity sendTroops(MilitaryUnitContract contract, VillageEntity village) {
        // deduct all involved units from village army
        var homeLegion = village.getHomeLegion();
        var attackingUnits = contract.getUnits();
        for (int i = 0; i < homeLegion.length; i++){
            homeLegion[i] = homeLegion[i] - attackingUnits[i];
        }
        // create MilitaryUnitEntity
        var militaryUnitEntity = new MilitaryUnitDynamic(contract.getNation(), true, contract.getMission(),
                contract.getOriginVillageId(), contract.getOriginVillageName(), contract.getOriginPlayerName(),
                contract.getOriginVillageCoordinates(), contract.getUnits(), contract.getArrivalTime(), contract.getTargetVillageId(),
                contract.getTargetVillageName(), contract.getTargetPlayerName(), contract.getTargetVillageCoordinates(), 120);
        militaryUnitRepository.save(militaryUnitEntity);
        return village;
    }

    @Override
    public List<CombatUnitDoneEvent> createCombatUnitDoneEventsFromOrders(String villageId) {

        List<CombatUnitDoneEvent> result = new ArrayList<>();
        List<OrderCombatUnitEntity> ordersList = combatUnitOrderRepository.findAllByVillageId(villageId);

        if (ordersList.size() > 0) {
            for (OrderCombatUnitEntity order : ordersList) {
                long duration = Duration.between(order.getLastTime(), LocalDateTime.now()).toSeconds();

                if (LocalDateTime.now().isAfter(order.getEndOrderTime())) {
                    // add all troops from order to result list
                    result.addAll(addCompletedCombatUnit(order, order.getLeftTrain()));
                    combatUnitOrderRepository.deleteById(order.getOrderId());
                    continue;
                }

                int completedTroops = (int) (duration / order.getDurationEach());

                if (completedTroops > 0) {
                    // add completed troops from order to result list
                    result.addAll(addCompletedCombatUnit(order, completedTroops));
                    order.setLeftTrain(order.getLeftTrain() - completedTroops);
                    order.setLastTime(order.getLastTime().plus(completedTroops * order.getDurationEach(), ChronoUnit.SECONDS));
                    combatUnitOrderRepository.save(order);
                }
            }
        }
        return result;
    }

    private List<CombatUnitDoneEvent> addCompletedCombatUnit(OrderCombatUnitEntity order, Integer amount) {
        List<CombatUnitDoneEvent> result = new ArrayList<>();
        LocalDateTime exec = order.getLastTime();
        for (int i = 0; i < amount; i++) {
            exec = exec.plus(order.getDurationEach(), ChronoUnit.SECONDS);
            result.add(new CombatUnitDoneEvent(exec, order.getUnitType(), order.getEatHour()));
        }
        return result;
    }
}
