package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.enums.EManipulation;
import io.lanu.travian.enums.EMilitaryUnitState;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.CombatUnitDoneEventEntity;
import io.lanu.travian.game.entities.events.MilitaryUnitEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.models.events.CombatUnitDoneStrategy;
import io.lanu.travian.game.models.events.EventStrategy;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.*;
import io.lanu.travian.game.repositories.CombatUnitOrderRepository;
import io.lanu.travian.game.repositories.MilitaryUnitRepository;
import io.lanu.travian.game.repositories.MovedMilitaryUnitRepository;
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
    private final MilitaryUnitRepository militaryUnitRepository;
    private final MovedMilitaryUnitRepository movedMilitaryUnitRepository;
    private final UsersRepository usersRepository;

    public MilitaryServiceImpl(CombatUnitOrderRepository combatUnitOrderRepository,
                               ResearchedCombatUnitRepository researchedCombatUnitRepository,
                               MilitaryUnitRepository militaryUnitRepository, MovedMilitaryUnitRepository movedMilitaryUnitRepository, UsersRepository usersRepository) {
        this.combatUnitOrderRepository = combatUnitOrderRepository;
        this.researchedCombatUnitRepository = researchedCombatUnitRepository;
        this.militaryUnitRepository = militaryUnitRepository;
        this.movedMilitaryUnitRepository = movedMilitaryUnitRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public MilitaryUnitEntity saveMilitaryUnit(MilitaryUnitEntity unit) {
        return militaryUnitRepository.save(unit);
    }

    @Override
    public MovedMilitaryUnitEntity saveMovedMilitaryUnit(MovedMilitaryUnitEntity unit) {
        return null;
    }

    @Override
    public void deleteMovedUnitById(String id) {
        movedMilitaryUnitRepository.deleteById(id);
    }

    @Override
    public void deleteUnitById(String id) {
        militaryUnitRepository.deleteById(id);
    }

    @Override
    public List<MilitaryUnitEntity> getAllByTargetVillageId(String villageId) {
        return militaryUnitRepository.getAllByTargetVillageId(villageId);
    }

    @Override
    public List<MovedMilitaryUnitEntity> getAllMovedUnitsByOriginVillageId(String villageId) {
        var result = movedMilitaryUnitRepository.getAllByOriginVillageId(villageId);
        //movedMilitaryUnitRepository.deleteAllByOriginVillageIdAndExecutionTimeBefore(villageId, LocalDateTime.now());
        return result;
    }

    @Override
    public Map<String, List<MilitaryUnitView>> getAllMilitaryUnitsByVillage(VillageEntity village) {
        var userName = usersRepository.findByUserId(village.getAccountId()).orElseThrow();
        var villageId = village.getVillageId();
        // other units
        List<MilitaryUnitView> unitsList = movedMilitaryUnitRepository.getAllByOriginVillageIdOrTargetVillageId(villageId, villageId)
                .stream()
                .map(mEv -> new MilitaryUnitViewDynamic(mEv.getId(), mEv.getNation(), true, null,
                                new VillageBrief(mEv.getOriginVillageId(), mEv.getOrigin().getVillageName(),
                                        mEv.getOrigin().getPlayerName(), mEv.getOrigin().getCoordinates()),
                                mEv.getUnits(), mEv.getMission(),
                                new VillageBrief(mEv.getTargetVillageId(), mEv.getTarget().getVillageName(),
                                        mEv.getTarget().getPlayerName(), mEv.getTarget().getCoordinates()),
                                mEv.getExecutionTime(), mEv.getDuration()))
                .peek(mU -> {
                    if (mU.getOriginVillage().getVillageId().equals(villageId)){
                        mU.setState(EMilitaryUnitState.OUT);
                    }else {
                        mU.setState(EMilitaryUnitState.IN);
                    }
                })
                .collect(Collectors.toList());

        unitsList.addAll(
                militaryUnitRepository.getAllByOriginVillageIdOrTargetVillageId(villageId, villageId).stream()
                    .map(mEv -> new MilitaryUnitViewStatic(mEv.getId(), mEv.getNation(), false, null,
                            new VillageBrief(mEv.getOriginVillageId(), mEv.getOrigin().getVillageName(),
                                    mEv.getOrigin().getPlayerName(), mEv.getOrigin().getCoordinates()),
                            mEv.getUnits(), mEv.getTargetVillageId(), mEv.getEatExpenses()))
                        .peek(mU -> {
                            if (mU.getOriginVillage().getVillageId().equals(villageId)){
                                mU.setState(EMilitaryUnitState.AWAY);
                            }else {
                                mU.setState(EMilitaryUnitState.HOME);
                            }
                        })
                        .collect(Collectors.toList()));

        Map<String, List<MilitaryUnitView>> militaryUnitsMap = unitsList.stream()
                .collect(Collectors.groupingBy(militaryEvent -> militaryEvent.getState().getName()));

        // home army
        MilitaryUnitView homeArmy = new MilitaryUnitViewStatic("home", village.getNation(), false, EMilitaryUnitState.HOME,
                new VillageBrief(villageId, village.getName(), userName.getUsername(), new int[]{village.getX(), village.getY()}),
                village.getHomeLegion(), villageId, 5);

        var homeArmies = militaryUnitsMap.getOrDefault(EMilitaryUnitState.HOME.getName(), new ArrayList<>());
        homeArmies.add(homeArmy);
        militaryUnitsMap.put(EMilitaryUnitState.HOME.getName(), homeArmies);
        return militaryUnitsMap;
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
        List<OrderCombatUnitEntity> ordersList = combatUnitOrderRepository
                .findAllByVillageId(orderCombatUnitRequest.getVillageId())
                .stream()
                .sorted(Comparator.comparing(OrderCombatUnitEntity::getCreated))
                .collect(Collectors.toList());

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
    public List<CombatUnitOrderResponse> getAllOrdersByVillageId(String villageId){
        return combatUnitOrderRepository
                .findAllByVillageId(villageId)
                .stream()
                .sorted(Comparator.comparing(OrderCombatUnitEntity::getCreated))
                .map(armyOrderEntity -> {
                    var duration = Duration.between(LocalDateTime.now(), armyOrderEntity.getEndOrderTime()).toSeconds();
                    return new CombatUnitOrderResponse(
                            armyOrderEntity.getUnitType().getName(),
                            armyOrderEntity.getLeftTrain(),
                            duration,
                            armyOrderEntity.getDurationEach(),
                            armyOrderEntity.getEndOrderTime());})
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
                .arrivalTime(LocalDateTime.now().minusHours(6).plusMinutes(2))
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
        var moveUnit = new MovedMilitaryUnitEntity(
                contract.getNation(), contract.getMission(), contract.getUnits(), contract.getOriginVillageId(),
                new VillageBrief(contract.getOriginVillageName(), contract.getOriginPlayerName(), contract.getOriginVillageCoordinates()),
                contract.getTargetVillageId(), new VillageBrief(contract.getTargetVillageName(), contract.getTargetPlayerName(), contract.getTargetVillageCoordinates()),
                contract.getArrivalTime(), 60, 0);
        movedMilitaryUnitRepository.save(moveUnit);
        return village;
    }

    @Override
    public List<EventStrategy> createCombatUnitDoneEventsFromOrders(VillageEntity origin) {

        List<EventStrategy> result = new ArrayList<>();
        var ordersList = combatUnitOrderRepository.findAllByVillageId(origin.getVillageId());

        if (ordersList.size() > 0) {
            for (OrderCombatUnitEntity order : ordersList) {
                long duration = Duration.between(order.getLastTime(), LocalDateTime.now()).toSeconds();

                if (LocalDateTime.now().isAfter(order.getEndOrderTime())) {
                    // add all troops from order to result list
                    result.addAll(addCompletedCombatUnit(origin, order, order.getLeftTrain()));
                    combatUnitOrderRepository.deleteById(order.getOrderId());
                    continue;
                }

                int completedTroops = (int) (duration / order.getDurationEach());

                if (completedTroops > 0) {
                    // add completed troops from order to result list
                    result.addAll(addCompletedCombatUnit(origin, order, completedTroops));
                    order.setLeftTrain(order.getLeftTrain() - completedTroops);
                    order.setLastTime(order.getLastTime().plus(completedTroops * order.getDurationEach(), ChronoUnit.SECONDS));
                    combatUnitOrderRepository.save(order);
                }
            }
        }
        return result;
    }

    private List<EventStrategy> addCompletedCombatUnit(VillageEntity origin, OrderCombatUnitEntity order, Integer amount) {
        List<EventStrategy> result = new ArrayList<>();
        LocalDateTime exec = order.getLastTime();
        for (int i = 0; i < amount; i++) {
            exec = exec.plus(order.getDurationEach(), ChronoUnit.SECONDS);
            result.add(new CombatUnitDoneStrategy(origin, new CombatUnitDoneEventEntity(exec, order.getUnitType(), order.getEatHour())));
        }
        return result;
    }


}
