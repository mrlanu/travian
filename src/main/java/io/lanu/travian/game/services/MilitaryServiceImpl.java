package io.lanu.travian.game.services;

import io.lanu.travian.Consts;
import io.lanu.travian.enums.*;
import io.lanu.travian.errors.UserErrorException;
import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.CombatGroupSendingRequest;
import io.lanu.travian.game.models.responses.*;
import io.lanu.travian.game.repositories.CombatGroupRepository;
import io.lanu.travian.game.repositories.CombatUnitOrderRepository;
import io.lanu.travian.game.repositories.ResearchedCombatUnitRepository;
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
    private final SettlementRepository settlementRepository;

    public MilitaryServiceImpl(CombatUnitOrderRepository combatUnitOrderRepository,
                               ResearchedCombatUnitRepository researchedCombatUnitRepository,
                               CombatGroupRepository combatGroupRepository,
                               SettlementRepository settlementRepository) {
        this.combatUnitOrderRepository = combatUnitOrderRepository;
        this.researchedCombatUnitRepository = researchedCombatUnitRepository;
        this.combatGroupRepository = combatGroupRepository;
        this.settlementRepository = settlementRepository;
    }

    @Override
    public Map<String, TroopMovementsBrief> getTroopMovementsBrief(String settlementId) {
        var result = Map.of(
                "Incoming Reinforcements", new TroopMovementsBrief(),
                "Incoming Attacks", new TroopMovementsBrief(),
                "Outgoing Reinforcements", new TroopMovementsBrief(),
                "Outgoing Attacks", new TroopMovementsBrief()
        );
        combatGroupRepository.getCombatGroupByOwnerSettlementIdOrToSettlementId(settlementId, settlementId)
                .stream()
                .sorted(Comparator.comparing(CombatGroupEntity::getExecutionTime))
                .forEach(cG -> {
                    if (cG.isMoved()) {
                        TroopMovementsBrief r;
                        //INCOMING
                        if (settlementId.equals(cG.getToSettlementId())) {
                            //REINFORCEMENT
                            if (cG.getMission().equals(ECombatGroupMission.BACK) || cG.getMission().equals(ECombatGroupMission.REINFORCEMENT)) {
                                r = result.get("Incoming Reinforcements");
                                //ATTACK & RAID
                            } else {
                                r = result.get("Incoming Attacks");
                            }
                        //OUTGOING
                        } else {
                            //REINFORCEMENT
                            if (cG.getMission().equals(ECombatGroupMission.BACK) || cG.getMission().equals(ECombatGroupMission.REINFORCEMENT)) {
                                r = result.get("Outgoing Reinforcements");
                            //ATTACK & RAID
                            } else {
                                r = result.get("Outgoing Attacks");
                            }
                        }
                        r.incrementCount();
                        r.setTimeToArrive((int) Duration.between(LocalDateTime.now(), cG.getExecutionTime()).toSeconds());
                    }
                });
        return result;
    }

    @Override
    public Map<String, List<CombatGroupView>> getAllMilitaryUnitsByVillage(SettlementEntity village) {
        var cache = new HashMap<String, SettlementEntity>();

        // other units
        var debug = combatGroupRepository
                .getCombatGroupByOwnerSettlementIdOrToSettlementId(village.getId(), village.getId());
        List<CombatGroupView> unitsList = debug
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
                        return new CombatGroupMovedView(cG.getId(), cG.getNation(), cG.getMission(), true, null,
                                new VillageBrief(from.getId(), from.getName(), cG.getOwnerUserName(), new int[]{from.getX(), from.getY()}),
                                new VillageBrief(to.getId(), to.getName(), village.getOwnerUserName(), new int[]{to.getX(), to.getY()}),
                                cG.getUnits(), cG.getPlunder(), cG.getExecutionTime(),
                                (int) Duration.between(LocalDateTime.now(), cG.getExecutionTime()).toSeconds());
                    } else {
                        return new CombatGroupStaticView(cG.getId(), cG.getNation(), cG.getMission(), false, null,
                                new VillageBrief(from.getId(), from.getName(), cG.getOwnerUserName(), new int[]{from.getX(), from.getY()}),
                                new VillageBrief(to.getId(), to.getName(), village.getOwnerUserName(), new int[]{to.getX(), to.getY()}),
                                cG.getUnits(), to.getId(), 5);
                    }
                })
                .peek(cG -> {
                    if (cG.getTo().getVillageId().equals(village.getId())) {
                        if (cG.isMove()) {
                            cG.setState(ECombatGroupLocation.IN);
                        } else {
                            cG.setState(ECombatGroupLocation.HOME);
                        }
                    } else {
                        if (cG.isMove()) {
                            cG.setState(ECombatGroupLocation.OUT);
                        } else {
                            cG.setState(ECombatGroupLocation.AWAY);
                        }
                    }
                })
                .collect(Collectors.toList());

        Map<String, List<CombatGroupView>> militaryUnitsMap = unitsList.stream()
                .collect(Collectors.groupingBy(militaryEvent -> militaryEvent.getState().getName()));

        // home army
        CombatGroupView homeArmy = new CombatGroupStaticView("home", village.getNation(), ECombatGroupMission.HOME,
                false, ECombatGroupLocation.HOME,
                new VillageBrief(village.getId(), village.getName(), village.getOwnerUserName(), new int[]{village.getX(), village.getY()}),
                new VillageBrief(village.getId(), village.getName(), village.getOwnerUserName(), new int[]{village.getX(), village.getY()}),
                village.getHomeLegion(), village.getId(), 5);

        var homeArmies = militaryUnitsMap.getOrDefault(ECombatGroupLocation.HOME.getName(), new ArrayList<>());
        homeArmies.add(homeArmy);
        militaryUnitsMap.put(ECombatGroupLocation.HOME.getName(), homeArmies);
        return militaryUnitsMap;
    }

    @Override
    public CombatGroupSendingContract checkTroopsSendingRequest(SettlementEntity settlementEntity, CombatGroupSendingRequest combatGroupSendingRequest) {
        var attackedVillageOpt = settlementRepository
                .findVillageByCoordinates(combatGroupSendingRequest.getX(), combatGroupSendingRequest.getY());
        if (attackedVillageOpt.isPresent()) {
            return checkTroopsSendingRequest(combatGroupSendingRequest, settlementEntity, attackedVillageOpt.get());
        } else {
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
    public SettlementEntity orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest, SettlementEntity settlement) {

        ECombatUnit unit = orderCombatUnitRequest.getUnitType();
        List<OrderCombatUnitEntity> ordersList = settlement.getCombatUnitOrders()
                .stream()
                .sorted(Comparator.comparing(OrderCombatUnitEntity::getCreated))
                .collect(Collectors.toList());

        LocalDateTime lastTime = ordersList.size() > 0 ? ordersList.get(ordersList.size() - 1).getEndOrderTime() : LocalDateTime.now();

        LocalDateTime endOrderTime = lastTime.plus(
                orderCombatUnitRequest.getAmount() * unit.getTime(), ChronoUnit.SECONDS);

        OrderCombatUnitEntity armyOrder = new OrderCombatUnitEntity(orderCombatUnitRequest.getVillageId(), lastTime, orderCombatUnitRequest.getUnitType(),
                orderCombatUnitRequest.getAmount(), unit.getTime(), unit.getEat(), endOrderTime);

        spendResources(orderCombatUnitRequest.getAmount(), settlement, unit);

        armyOrder.setCreated(LocalDateTime.now());
        ordersList.add(armyOrder);
        settlement.setCombatUnitOrders(ordersList);

        return settlement;
    }

    private void spendResources(int unitsAmount, SettlementEntity settlementEntity, ECombatUnit kind) {
        Map<EResource, BigDecimal> neededResources = new HashMap<>();
        kind.getCost().forEach((k, v) -> neededResources.put(k, BigDecimal.valueOf((long) v * unitsAmount)));
        settlementEntity.manipulateGoods(EManipulation.SUBTRACT, neededResources);
    }

    private CombatGroupSendingContract checkTroopsSendingRequest(CombatGroupSendingRequest combatGroupSendingRequest,
                                                                 SettlementEntity attackingVillage,
                                                                 SettlementEntity attackedVillage) {

        String attackedUser = attackedVillage.getOwnerUserName() == null ? "Nature" : attackedVillage.getOwnerUserName();

        var duration = getDistance(attackedVillage.getX(), attackedVillage.getY(), attackingVillage.getX(), attackingVillage.getY())
                .multiply(BigDecimal.valueOf(3600)
                        .divide(BigDecimal.valueOf(10 * Consts.SPEED), MathContext.DECIMAL32)).intValue();
        var arrivalTime = LocalDateTime.now().plusSeconds(duration);
        return CombatGroupSendingContract.builder()
                .nation(attackingVillage.getNation())
                .mission(combatGroupSendingRequest.getMission())
                .originVillageId(attackingVillage.getId())
                .originVillageName(attackingVillage.getName())
                .originPlayerName(attackingVillage.getOwnerUserName())
                .originVillageCoordinates(new int[]{attackingVillage.getX(), attackingVillage.getY()})
                .targetVillageId(attackedVillage.getId())
                .targetVillageName(attackedVillage.getName())
                .targetPlayerName(attackedUser)
                .targetVillageCoordinates(new int[]{attackedVillage.getX(), attackedVillage.getY()})
                .units(combatGroupSendingRequest.getWaves().get(0).getTroops())
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
    public SettlementEntity sendTroops(CombatGroupSendingContract contract, SettlementEntity village) {
        // deduct all involved units from village army
        var homeLegion = village.getHomeLegion();
        var attackingUnits = contract.getUnits();
        for (int i = 0; i < homeLegion.length; i++) {
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
