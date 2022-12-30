package io.lanu.travian.game.services;

import io.lanu.travian.Consts;
import io.lanu.travian.enums.*;
import io.lanu.travian.game.entities.*;
import io.lanu.travian.game.models.requests.CombatGroupSendingRequest;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.responses.*;
import io.lanu.travian.game.repositories.CombatGroupContractRepository;
import io.lanu.travian.game.repositories.CombatGroupRepository;
import io.lanu.travian.game.repositories.ResearchedCombatUnitRepository;
import io.lanu.travian.game.repositories.SettlementRepository;
import io.lanu.travian.templates.military.CombatUnitFactory;
import org.modelmapper.ModelMapper;
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
    private final ResearchedCombatUnitRepository researchedCombatUnitRepository;
    private final CombatGroupRepository combatGroupRepository;
    private final SettlementRepository settlementRepository;
    private final CombatGroupContractRepository combatGroupContractRepository;
    private final EngineService engineService;

    public MilitaryServiceImpl(ResearchedCombatUnitRepository researchedCombatUnitRepository,
                               CombatGroupRepository combatGroupRepository,
                               SettlementRepository settlementRepository,
                               CombatGroupContractRepository combatGroupContractRepository,
                               EngineService engineService) {
        this.researchedCombatUnitRepository = researchedCombatUnitRepository;
        this.combatGroupRepository = combatGroupRepository;
        this.settlementRepository = settlementRepository;
        this.combatGroupContractRepository = combatGroupContractRepository;
        this.engineService = engineService;
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
    public Map<ECombatGroupLocation, List<CombatGroupView>> getAllCombatGroupsByVillage(SettlementEntity settlement) {
        var cache = new HashMap<String, SettlementEntity>();

        // other units
        List<CombatGroupView> unitsList = combatGroupRepository
                .getCombatGroupByOwnerSettlementIdOrToSettlementId(settlement.getId(), settlement.getId())
                .stream()
                .sorted(Comparator.comparing(CombatGroupEntity::getExecutionTime))
                .map(cG -> {
                    SettlementEntity from;
                    SettlementEntity to;
                    if (cache.containsKey(cG.getFromSettlementId())) {
                        from = cache.get(cG.getFromSettlementId());
                    } else {
                        from = settlementRepository.findById(cG.getFromSettlementId()).orElseThrow();
                        cache.put(from.getId(), from);
                    }
                    if (cache.containsKey(cG.getToSettlementId())) {
                        to = cache.get(cG.getToSettlementId());
                    } else {
                        to = settlementRepository.findById(cG.getToSettlementId()).orElseThrow();
                        cache.put(to.getId(), to);
                    }

                    if (cG.isMoved()) {
                        ENation nation = settlement.getNation();
                        if (cG.getToSettlementId().equals(settlement.getId())) {
                            nation = settlement.getNation();
                        }
                        return new CombatGroupMovedView(cG.getId(), nation, cG.getMission(), true, null,
                                new VillageBrief(from.getId(), from.getName(), from.getOwnerUserName(), new int[]{from.getX(), from.getY()}),
                                new VillageBrief(to.getId(), to.getName(), to.getOwnerUserName(), new int[]{to.getX(), to.getY()}),
                                cG.getUnits(), cG.getPlunder(), cG.getExecutionTime(),
                                (int) Duration.between(LocalDateTime.now(), cG.getExecutionTime()).toSeconds());
                    } else {
                        return new CombatGroupStaticView(cG.getId(), settlement.getNation(), cG.getMission(), false, null,
                                new VillageBrief(from.getId(), from.getName(), from.getOwnerUserName(), new int[]{from.getX(), from.getY()}),
                                new VillageBrief(to.getId(), to.getName(), to.getOwnerUserName(), new int[]{to.getX(), to.getY()}),
                                cG.getUnits(), to.getId(), 5);
                    }
                })
                .peek(cG -> {
                    if (cG.getTo().getVillageId().equals(settlement.getId())) {
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

        Map<ECombatGroupLocation, List<CombatGroupView>> militaryUnitsMap = unitsList.stream()
                .collect(Collectors.groupingBy(CombatGroupView::getState));

        // home army
        CombatGroupView homeArmy = new CombatGroupStaticView("home", settlement.getNation(), ECombatGroupMission.HOME,
                false, ECombatGroupLocation.HOME,
                new VillageBrief(settlement.getId(), settlement.getName(), settlement.getOwnerUserName(), new int[]{settlement.getX(), settlement.getY()}),
                new VillageBrief(settlement.getId(), settlement.getName(), settlement.getOwnerUserName(), new int[]{settlement.getX(), settlement.getY()}),
                settlement.getHomeLegion(), settlement.getId(), 5);

        var homeArmies = militaryUnitsMap.getOrDefault(ECombatGroupLocation.HOME, new ArrayList<>());
        homeArmies.add(homeArmy);
        militaryUnitsMap.put(ECombatGroupLocation.HOME, homeArmies);

        if (!militaryUnitsMap.containsKey(ECombatGroupLocation.IN)){
            militaryUnitsMap.put(ECombatGroupLocation.IN, new ArrayList<>());
        }
        if (!militaryUnitsMap.containsKey(ECombatGroupLocation.OUT)){
            militaryUnitsMap.put(ECombatGroupLocation.OUT, new ArrayList<>());
        }
        if (!militaryUnitsMap.containsKey(ECombatGroupLocation.AWAY)){
            militaryUnitsMap.put(ECombatGroupLocation.AWAY, new ArrayList<>());
        }

        return militaryUnitsMap;
    }

    @Override
    public CombatGroupContractResponse checkTroopsSendingRequest(SettlementEntity settlementState,
                                                                 SettlementEntity targetState,
                                                                 CombatGroupSendingRequest combatGroupSendingRequest) {
        //delete all expired contracts (executed or canceled)
        combatGroupContractRepository.deleteAllByOwnerSettlementId(settlementState.getId());

        var duration = getDistance(targetState.getX(), targetState.getY(),
                settlementState.getX(), settlementState.getY()).multiply(BigDecimal.valueOf(3600)
                        .divide(BigDecimal.valueOf(10 * Consts.SPEED), MathContext.DECIMAL32)).intValue();
        var arrivalTime = LocalDateTime.now().plusSeconds(duration);
        var combatGroupContractEntity = combatGroupContractRepository
                .save(new CombatGroupContractEntity(null, settlementState.getId(), combatGroupSendingRequest.getMission(),
                targetState.getId(), combatGroupSendingRequest.getWaves().get(0).getTroops(), arrivalTime, duration));

        return CombatGroupContractResponse.builder()
                .savedEntityId(combatGroupContractEntity.getId())
                .mission(combatGroupSendingRequest.getMission())
                .targetVillageId(targetState.getId())
                .targetVillageName(targetState.getName())
                .targetPlayerName(targetState.getOwnerUserName())
                .targetVillageCoordinates(new int[]{targetState.getX(), targetState.getY()})
                .units(combatGroupSendingRequest.getWaves().get(0).getTroops()) //delete ?
                .arrivalTime(arrivalTime)
                .duration(duration)
                .build();
    }

    @Override
    public List<CombatUnitResponse> getAllResearchedUnits(String villageId) {
        return researchedCombatUnitRepository.findByVillageId(villageId).getUnits()
                .stream()
                .map(shortUnit -> CombatUnitFactory.getUnit(shortUnit.getName(), shortUnit.getLevel()))
                .collect(Collectors.toList());
    }

    @Override
    public SettlementEntity orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest, String settlementId) {
        var currentState = engineService.recalculateCurrentState(settlementId, LocalDateTime.now());
        ECombatUnit unit = orderCombatUnitRequest.getUnitType();
        ModelMapper mapper = new ModelMapper();
        CombatUnitResponse mappedUnit = mapper.map(unit, CombatUnitResponse.class);
        mappedUnit.setSpeed(mappedUnit.getSpeed() / Consts.SPEED);
        mappedUnit.setTime(mappedUnit.getTime() / Consts.SPEED);

        List<OrderCombatUnitEntity> ordersList = currentState.getCombatUnitOrders()
                .stream()
                .sorted(Comparator.comparing(OrderCombatUnitEntity::getCreated))
                .collect(Collectors.toList());

        LocalDateTime lastTime = ordersList.size() > 0 ? ordersList.get(ordersList.size() - 1).getEndOrderTime() : LocalDateTime.now();

        LocalDateTime endOrderTime = lastTime.plus(
                orderCombatUnitRequest.getAmount() * mappedUnit.getTime(), ChronoUnit.SECONDS);

        OrderCombatUnitEntity armyOrder = new OrderCombatUnitEntity(orderCombatUnitRequest.getVillageId(), lastTime,
                orderCombatUnitRequest.getUnitType(), orderCombatUnitRequest.getAmount(), mappedUnit.getTime(), mappedUnit.getEat(),
                endOrderTime);

        spendResources(orderCombatUnitRequest.getAmount(), currentState, mappedUnit);

        armyOrder.setCreated(LocalDateTime.now());
        ordersList.add(armyOrder);
        currentState.setCombatUnitOrders(ordersList);

        return engineService.save(currentState);
    }

    private void spendResources(int unitsAmount, SettlementEntity settlementEntity, CombatUnitResponse kind) {
        Map<EResource, BigDecimal> neededResources = new HashMap<>();
        kind.getCost().forEach((k, v) -> neededResources.put(k, BigDecimal.valueOf((long) v * unitsAmount)));
        settlementEntity.manipulateGoods(EManipulation.SUBTRACT, neededResources);
    }

    public static BigDecimal getDistance(int x, int y, int fromX, int fromY) {
        var legX = BigDecimal.valueOf(x - fromX).pow(2);
        var legY = BigDecimal.valueOf(y - fromY).pow(2);
        return legX.add(legY).sqrt(new MathContext(2));
    }

    @Override
    public SettlementEntity sendTroops(SettlementEntity settlementState, String contractId) {
        // deduct all involved units from village army
        var homeLegion = settlementState.getHomeLegion();
        var contractEntity = combatGroupContractRepository.findById(contractId).orElseThrow();
        var attackingUnits = contractEntity.getUnits();
        for (int i = 0; i < homeLegion.length; i++) {
            homeLegion[i] = homeLegion[i] - attackingUnits[i];
        }

        var combatGroup = CombatGroupEntity.builder()
                .moved(true)
                .ownerSettlementId(settlementState.getId())
                .ownerNation(settlementState.getNation())
                .fromSettlementId(settlementState.getId())
                .toSettlementId(contractEntity.getTargetVillageId())
                .executionTime(LocalDateTime.now().plusSeconds(contractEntity.getDuration()))
                .duration(contractEntity.getDuration())
                .mission(contractEntity.getMission())
                .units(contractEntity.getUnits())
                .build();

        combatGroupRepository.save(combatGroup);
        return settlementState;
    }
}
