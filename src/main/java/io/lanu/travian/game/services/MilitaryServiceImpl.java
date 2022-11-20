package io.lanu.travian.game.services;

import io.lanu.travian.Consts;
import io.lanu.travian.enums.*;
import io.lanu.travian.errors.UserErrorException;
import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.*;
import io.lanu.travian.game.repositories.*;
import io.lanu.travian.security.UsersRepository;
import io.lanu.travian.templates.military.CombatUnitFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MilitaryServiceImpl implements MilitaryService {

    private final CombatUnitOrderRepository combatUnitOrderRepository;
    private final ResearchedCombatUnitRepository researchedCombatUnitRepository;
    private final CombatGroupRepository combatGroupRepository;
    private final UsersRepository usersRepository;
    private final SettlementRepository settlementRepository;

    public MilitaryServiceImpl(CombatUnitOrderRepository combatUnitOrderRepository,
                               ResearchedCombatUnitRepository researchedCombatUnitRepository,
                               CombatGroupRepository combatGroupRepository, UsersRepository usersRepository,
                               SettlementRepository settlementRepository) {
        this.combatUnitOrderRepository = combatUnitOrderRepository;
        this.researchedCombatUnitRepository = researchedCombatUnitRepository;
        this.combatGroupRepository = combatGroupRepository;
        this.usersRepository = usersRepository;
        this.settlementRepository = settlementRepository;
    }

    @Override
    public Map<String, List<MilitaryUnitView>> getAllMilitaryUnitsByVillage(SettlementEntity village) {
        var userName = usersRepository.findByUserId(village.getAccountId()).orElseThrow();
        var cache = new HashMap<String, SettlementEntity>();

        // other units
        List<MilitaryUnitView> unitsList = combatGroupRepository
                .getCombatGroupByOwnerSettlementIdOrToSettlementId(village.getId(), village.getId())
                .stream()
                .map(cG -> {
                    SettlementEntity from;
                    SettlementEntity to;
                    if (cache.containsKey(cG.getOwnerSettlementId())) {
                        from = cache.get(cG.getOwnerSettlementId());
                    } else {
                        from = settlementRepository.findById(cG.getOwnerSettlementId());
                        cache.put(from.getId(), from);
                    }
                    if (cache.containsKey(cG.getToSettlementId())) {
                        to = cache.get(cG.getToSettlementId());
                    } else {
                        to = settlementRepository.findById(cG.getToSettlementId());
                        cache.put(to.getId(), to);
                    }

                    if (cG.isMoved()) {
                        return new MovedMilitaryUnitView(cG.getId(), cG.getNation(), cG.getMission(), true, null,
                                new VillageBrief(from.getId(), from.getName(), cG.getOwnerUserName(), new int[]{from.getX(), from.getY()}),
                                new VillageBrief(to.getId(), to.getName(), village.getOwnerUserName(), new int[]{to.getX(), to.getY()}),
                                cG.getUnits(), cG.getPlunder(), cG.getExecutionTime(),
                                (int) Duration.between(LocalDateTime.now(), cG.getExecutionTime()).toSeconds());
                    } else {
                        return new MilitaryUnitViewStatic(cG.getId(), cG.getNation(), cG.getMission(), false, null,
                                new VillageBrief(from.getId(), from.getName(), cG.getOwnerUserName(), new int[]{from.getX(), from.getY()}),
                                new VillageBrief(to.getId(), to.getName(), village.getOwnerUserName(), new int[]{to.getX(), to.getY()}),
                                cG.getUnits(), to.getId(), 5);
                    }
                })
                .peek(cG -> {
                    if (cG.getOrigin().getVillageId().equals(village.getId())){
                        if (cG.isMove()) {
                            cG.setState(EMilitaryUnitLocation.OUT);
                        } else {
                            cG.setState(EMilitaryUnitLocation.AWAY);
                        }
                    }else {
                        if (cG.isMove()){
                            cG.setState(EMilitaryUnitLocation.IN);
                        }else {
                            cG.setState(EMilitaryUnitLocation.HOME);
                        }
                    }
                })
                .collect(Collectors.toList());

        Map<String, List<MilitaryUnitView>> militaryUnitsMap = unitsList.stream()
                .collect(Collectors.groupingBy(militaryEvent -> militaryEvent.getState().getName()));

        // home army
        MilitaryUnitView homeArmy = new MilitaryUnitViewStatic("home", village.getNation(), ECombatUnitMission.HOME,
                false, EMilitaryUnitLocation.HOME,
                new VillageBrief(village.getId(), village.getName(), userName.getUsername(), new int[]{village.getX(), village.getY()}),
                new VillageBrief(village.getId(), village.getName(), userName.getUsername(), new int[]{village.getX(), village.getY()}),
                village.getHomeLegion(), village.getId(), 5);

        var homeArmies = militaryUnitsMap.getOrDefault(EMilitaryUnitLocation.HOME.getName(), new ArrayList<>());
        homeArmies.add(homeArmy);
        militaryUnitsMap.put(EMilitaryUnitLocation.HOME.getName(), homeArmies);
        return militaryUnitsMap;
    }

    @Override
    public List<TroopMovementsResponse> getTroopMovements(SettlementEntity settlement) {
        List<TroopMovementsResponse> result = new ArrayList<>();
        //sort outgoing (true) & incoming (false)
        var movedUnits = combatGroupRepository
                .getCombatGroupByOwnerSettlementIdOrToSettlementId(settlement.getId(), settlement.getId())
                .stream()
                .sorted(Comparator.comparing(CombatGroupEntity::getExecutionTime))
                .collect(Collectors.partitioningBy(m -> m.getOwnerSettlementId().equals(settlement.getId()),
                        // sort attacks (true) & reinforcements (false)
                        Collectors.groupingBy(m -> m.getMission().equals(ECombatUnitMission.ATTACK.getName()) ||
                                m.getMission().equals(ECombatUnitMission.RAID.getName()))));

        // outgoing
        // attacks & raids
        if (movedUnits.get(true).getOrDefault(true, new ArrayList<>()).size() > 0) {
            result.add(new TroopMovementsResponse(movedUnits.get(true).get(true).size(), ECombatUnitMission.ATTACK.getName(),
                    (int) Duration.between(LocalDateTime.now(), movedUnits.get(true).get(true).get(0).getExecutionTime()).toSeconds()));
        } else {
            result.add(new TroopMovementsResponse());
        }
        //reinforcements
        if (movedUnits.get(true).getOrDefault(false, new ArrayList<>()).size() > 0){
            result.add(new TroopMovementsResponse(movedUnits.get(true).get(false).size(), ECombatUnitMission.REINFORCEMENT.getName(),
                    (int) Duration.between(LocalDateTime.now(), movedUnits.get(true).get(false).get(0).getExecutionTime()).toSeconds()));
        } else {
            result.add(new TroopMovementsResponse());
        }

        //incoming
        // attacks & raids
        if (movedUnits.get(false).getOrDefault(true, new ArrayList<>()).size() > 0) {
            result.add(new TroopMovementsResponse(movedUnits.get(false).get(true).size(), ECombatUnitMission.ATTACK.getName(),
                    (int) Duration.between(LocalDateTime.now(), movedUnits.get(false).get(true).get(0).getExecutionTime()).toSeconds()));
        } else {
            result.add(new TroopMovementsResponse());
        }

    //reinforcements
        if (movedUnits.get(false).getOrDefault(false, new ArrayList<>()).size() > 0){
            result.add(new TroopMovementsResponse(movedUnits.get(false).get(false).size(), ECombatUnitMission.REINFORCEMENT.getName(),
                    (int) Duration.between(LocalDateTime.now(), movedUnits.get(false).get(false).get(0).getExecutionTime()).toSeconds()));
        } else {
            result.add(new TroopMovementsResponse());
        }
        return result;
    }

    @Override
    public MilitaryUnitContract checkTroopsSendingRequest(SettlementEntity settlementEntity, TroopsSendingRequest troopsSendingRequest) {
        var attackedVillageOpt = settlementRepository
                .findVillageByCoordinates(troopsSendingRequest.getX(), troopsSendingRequest.getY());
        if (attackedVillageOpt.isPresent()) {
            return checkTroopsSendingRequest(troopsSendingRequest, settlementEntity, attackedVillageOpt.get());
        }else {
            throw new UserErrorException("There is nothing on those coordinates");
        }
    }

    @Override
    public List<ECombatUnit> getAllResearchedUnits(String villageId) {
        return researchedCombatUnitRepository.findByVillageId(villageId).getUnits()
                .stream()
                .map(shortUnit -> CombatUnitFactory.getUnit(shortUnit.getName(), shortUnit.getLevel()))
                .collect(Collectors.toList());
    }

    @Override
    public SettlementEntity orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest, SettlementEntity village) {

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

    private void spendResources(int unitsAmount, SettlementEntity settlementEntity, ECombatUnit kind) {
        Map<EResource, BigDecimal> neededResources = new HashMap<>();
        kind.getCost().forEach((k, v) -> neededResources.put(k, BigDecimal.valueOf((long) v * unitsAmount)));
        settlementEntity.manipulateGoods(EManipulation.SUBTRACT, neededResources);
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

    private MilitaryUnitContract checkTroopsSendingRequest(TroopsSendingRequest troopsSendingRequest, SettlementEntity attackingVillage, SettlementEntity attackedVillage) {

        String attackedUser = attackedVillage.getOwnerUserName() == null ? "Nature" : attackedVillage.getOwnerUserName();

        var duration = getDistance(attackedVillage.getX(), attackedVillage.getY(), attackingVillage.getX(), attackingVillage.getY())
                .multiply(BigDecimal.valueOf(3600)
                        .divide(BigDecimal.valueOf(10 * Consts.SPEED), MathContext.DECIMAL32)).intValue();
        var arrivalTime = LocalDateTime.now().plusSeconds(duration);
        return MilitaryUnitContract.builder()
                .nation(attackingVillage.getNation())
                .mission(troopsSendingRequest.getMission())
                .originVillageId(attackingVillage.getId())
                .originVillageName(attackingVillage.getName())
                .originPlayerName(attackingVillage.getOwnerUserName())
                .originVillageCoordinates(new int[]{attackingVillage.getX(), attackingVillage.getY()})
                .targetVillageId(attackedVillage.getId())
                .targetVillageName(attackedVillage.getName())
                .targetPlayerName(attackedUser)
                .targetVillageCoordinates(new int[]{attackedVillage.getX(), attackedVillage.getY()})
                .units(troopsSendingRequest.getWaves().get(0).getTroops())
                .arrivalTime(arrivalTime)
                .duration(duration)
                .build();
    }

    public static BigDecimal getDistance(int x, int y, int fromX, int fromY) {
        var legX = BigDecimal.valueOf(x - fromX).pow(2);
        var legY = BigDecimal.valueOf(y - fromY).pow(2);
        return legX.add(legY).sqrt(new MathContext(2));
    }

    @Override
    public SettlementEntity sendTroops(MilitaryUnitContract contract, SettlementEntity village) {
        // deduct all involved units from village army
        var homeLegion = village.getHomeLegion();
        var attackingUnits = contract.getUnits();
        for (int i = 0; i < homeLegion.length; i++){
            homeLegion[i] = homeLegion[i] - attackingUnits[i];
        }

        var combatGroup = CombatGroupEntity.builder()
                .moved(true)
                .nation(village.getNation())
                .ownerAccountId(village.getAccountId())
                .ownerUserName(village.getOwnerUserName())
                .ownerSettlementId(village.getId())
                .ownerSettlementName(village.getName())
                .toSettlementId(contract.getTargetVillageId())
                .executionTime(LocalDateTime.now().plusSeconds(contract.getDuration()))
                .duration(contract.getDuration())
                .mission(contract.getMission())
                .units(contract.getUnits())
                .build();

        combatGroupRepository.save(combatGroup);

        return village;
    }
}
