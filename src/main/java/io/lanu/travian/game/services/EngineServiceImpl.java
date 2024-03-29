package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatGroupLocation;
import io.lanu.travian.enums.ECombatGroupMission;
import io.lanu.travian.enums.ENation;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.dto.SettlementStateDTO;
import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.CombatUnitDoneEventEntity;
import io.lanu.travian.game.models.event.*;
import io.lanu.travian.game.models.responses.*;
import io.lanu.travian.game.repositories.CombatGroupRepository;
import io.lanu.travian.game.repositories.ReportRepository;
import io.lanu.travian.game.repositories.SettlementRepository;
import io.lanu.travian.game.repositories.StatisticsRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EngineServiceImpl implements EngineService {

    private static final MathContext mc = new MathContext(3);
    private final SettlementRepository settlementRepository;
    private final CombatGroupRepository combatGroupRepository;
    private final ReportRepository reportRepository;
    private final StatisticsRepository statisticsRepository;
    private final Set<String> cache = new HashSet<>();

    public EngineServiceImpl(SettlementRepository settlementRepository, CombatGroupRepository combatGroupRepository,
                             ReportRepository reportRepository, StatisticsRepository statisticsRepository) {
        this.settlementRepository = settlementRepository;
        this.combatGroupRepository = combatGroupRepository;
        this.reportRepository = reportRepository;
        this.statisticsRepository = statisticsRepository;
    }

    @Override
    public CombatGroupRepository getCombatGroupRepository() {
        return combatGroupRepository;
    }

    @Override
    public ReportRepository getReportRepository() {
        return reportRepository;
    }

    @Override
    public SettlementRepository getSettlementRepository() {
        return settlementRepository;
    }

    @Override
    public SettlementStateDTO updateAccount(String exceptSettlementId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accountId = authentication.getName();

        var cache = new HashSet<String>();
        var allCombatEvents = combatGroupRepository
                .getCombatGroupByFromAccountIdOrToAccountIdAndExecutionTimeBefore(accountId, accountId, LocalDateTime.now());
        allCombatEvents.forEach(cG -> {
            if (cG.getFromAccountId().equals(accountId)){
                if (!cG.getFromSettlementId().equals(exceptSettlementId)){
                    cache.add(cG.getFromSettlementId());
                }
            }else {
                if (!cG.getToSettlementId().equals(exceptSettlementId)){
                    cache.add(cG.getToSettlementId());
                }
            }
        });
        cache.forEach(id -> updateParticularSettlementState(id, LocalDateTime.now()));
        return updateParticularSettlementState(exceptSettlementId, LocalDateTime.now());
    }

    @Override
    public SettlementStateDTO updateParticularSettlementState(String settlementId, LocalDateTime untilTime) {
        while (cache.contains(settlementId)){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        cache.add(settlementId);
        var state = SettlementStateDTO
                .builder()
                .settlementEntity(settlementRepository.findById(settlementId).orElseThrow())
                .build();

        var allEvents = combineAllEvents(state.getSettlementEntity(), untilTime);
        executeAllEvents(state, allEvents);
        state.getSettlementEntity().setModifiedTime(untilTime);
        settlementRepository.save(state.getSettlementEntity());

        state.setCombatGroupsInSettlement(combatGroupRepository.getAllByToSettlementIdAndMoved(settlementId, false));
        state.setMovementsBriefMap(getTroopMovementsBrief(state.getSettlementEntity().getId()));
        state.setCombatGroupByLocationMap(getAllCombatGroupsByLocation(state.getSettlementEntity()));

        cache.remove(settlementId);
        return state;
    }

    @Override
    public SettlementStateDTO saveSettlementEntity(SettlementStateDTO currentState){
        while (cache.contains(currentState.getSettlementEntity().getId())){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        cache.add(currentState.getSettlementEntity().getId());
        currentState.getSettlementEntity().setModifiedTime(LocalDateTime.now());
        var result = settlementRepository.save(currentState.getSettlementEntity());
        cache.remove(currentState.getSettlementEntity().getId());
        return currentState;
    }

    private void executeAllEvents(SettlementStateDTO state, List<Event> allEvents) {
        var settlementEntity = state.getSettlementEntity();
        var modified = settlementEntity.getModifiedTime();
        for (Event event : allEvents) {
            var cropPerHour = settlementEntity.calculateProducePerHour().get(3);

            // if crop in the village is less than 0 keep create the death event & execute them until the crop will be positive
            while (cropPerHour.longValue() < 0) {
                var leftCrop = settlementEntity.getStorage().get(3);
                var durationToDeath = leftCrop.divide(cropPerHour.negate(), mc).multiply(BigDecimal.valueOf(3_600_000), mc);

                LocalDateTime deathTime = modified.plus(durationToDeath.longValue(), ChronoUnit.MILLIS);

                if (deathTime.isBefore(event.getExecutionTime())) {
                    Event deathEvent = new DeathEvent(deathTime);
                    settlementEntity.calculateProducedGoods(modified, deathEvent.getExecutionTime());
                    deathEvent.execute(state);
                    modified = deathEvent.getExecutionTime();
                } else {
                    break;
                }
                cropPerHour = settlementEntity.calculateProducePerHour().get(3);
            }
            // recalculate storage leftovers
            settlementEntity.calculateProducedGoods(modified, event.getExecutionTime());
            state.getSettlementEntity().castStorage();
            event.execute(state);
            modified = event.getExecutionTime();
        }
    }

    private List<Event> combineAllEvents(SettlementEntity currentSettlement, LocalDateTime untilTime) {

        // add all constructions events
        List<Event> allEvents = currentSettlement.getConstructionEventList().stream()
                .filter(event -> event.getExecutionTime().isBefore(untilTime))
                .map(event -> new ConstructionEvent(event, statisticsRepository))
                .collect(Collectors.toList());

        // add all units events
        var combatEventList = createCombatUnitDoneEventsFromOrders(currentSettlement, untilTime);
        allEvents.addAll(combatEventList);

        // add all combat groups arrived events
        var militaryEventList = combatGroupRepository
                .getCombatGroupByFromSettlementIdOrToSettlementId(currentSettlement.getId(), currentSettlement.getId())
                .stream()
                .filter(cG -> cG.isMoved() &&
                        cG.getExecutionTime().isBefore(untilTime) &&
                        !(currentSettlement.getId().equals(cG.getFromSettlementId()) &&
                                cG.getMission().equals(ECombatGroupMission.BACK)))
                .map(cG -> new TroopsArrivedEvent(cG, this))
                .collect(Collectors.toList());

        // add returning events after raids or attacks (just mocks, they will be edited later in events loop)
        List<TroopsArrivedEvent> militaryEventsWithReturn = new ArrayList<>();
        militaryEventList.forEach(mE -> {
            militaryEventsWithReturn.add(mE);
            if (!currentSettlement.getId().equals(mE.getCombatGroup().getToSettlementId())
                    && (mE.getCombatGroup().getMission().equals(ECombatGroupMission.ATTACK)
                        || mE.getCombatGroup().getMission().equals(ECombatGroupMission.RAID))
                    && mE.getExecutionTime().plusSeconds(mE.getCombatGroup().getDuration()).isBefore(untilTime)){
                var returningGroup = CombatGroupEntity
                        .builder()
                        .id(mE.getCombatGroup().getId())
                        .mission(ECombatGroupMission.BACK)
                        .executionTime(mE.getExecutionTime().plusSeconds(mE.getCombatGroup().getDuration()))
                        .build();
                militaryEventsWithReturn.add(new TroopsArrivedEvent(returningGroup, this));
            }
        });
        allEvents.addAll(militaryEventsWithReturn);

        // add last empty event
        allEvents.add(new LastEvent(untilTime));

        return allEvents.stream()
                .sorted(Comparator.comparing(Event::getExecutionTime))
                .collect(Collectors.toList());
    }

    private List<Event> createCombatUnitDoneEventsFromOrders(SettlementEntity settlement, LocalDateTime untilTime) {

        List<Event> result = new ArrayList<>();
        var ordersList = settlement.getCombatUnitOrders();
        List<OrderCombatUnitEntity> newOrdersList = new ArrayList<>();

        if (ordersList.size() > 0) {
            for (OrderCombatUnitEntity order : ordersList) {
                long duration = Duration.between(order.getLastTime(), untilTime).toSeconds();

                if (untilTime.isAfter(order.getEndOrderTime())) {
                    // add all troops from order to result list
                    result.addAll(addCompletedCombatUnit(order, order.getLeftTrain()));
                    continue;
                }

                int completedTroops = (int) (duration / order.getDurationEach());

                if (completedTroops > 0) {
                    // add completed troops from order to result list
                    result.addAll(addCompletedCombatUnit(order, completedTroops));
                    order.setLeftTrain(order.getLeftTrain() - completedTroops);
                    order.setLastTime(order.getLastTime().plus(completedTroops * order.getDurationEach(), ChronoUnit.SECONDS));
                }
                newOrdersList.add(order);
            }
        }
        settlement.setCombatUnitOrders(newOrdersList);
        return result;
    }

    private List<Event> addCompletedCombatUnit(OrderCombatUnitEntity order, Integer amount) {
        List<Event> result = new ArrayList<>();
        LocalDateTime exec = order.getLastTime();
        for (int i = 0; i < amount; i++) {
            exec = exec.plus(order.getDurationEach(), ChronoUnit.SECONDS);
            result.add(new CombatUnitDoneEvent(new CombatUnitDoneEventEntity(exec, order.getUnit())));
        }
        return result;
    }

    private Map<String, TroopMovementsBrief> getTroopMovementsBrief(String settlementId) {
        var result = Map.of(
                "Incoming Reinforcements", new TroopMovementsBrief(),
                "Incoming Attacks", new TroopMovementsBrief(),
                "Outgoing Reinforcements", new TroopMovementsBrief(),
                "Outgoing Attacks", new TroopMovementsBrief()
        );
        combatGroupRepository.getCombatGroupByFromSettlementIdOrToSettlementId(settlementId, settlementId)
                .stream()
                .sorted(Comparator.comparing(CombatGroupEntity::getExecutionTime))
                .filter(cG -> !(settlementId.equals(cG.getFromSettlementId()) &&
                        cG.getMission().equals(ECombatGroupMission.BACK)))
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

    private Map<ECombatGroupLocation, List<CombatGroupView>> getAllCombatGroupsByLocation(SettlementEntity settlement) {
        var cache = new HashMap<String, SettlementEntity>();

        // other units
        List<CombatGroupView> unitsList = combatGroupRepository
                .getCombatGroupByFromSettlementIdOrToSettlementId(settlement.getId(), settlement.getId())
                .stream()
                .sorted(Comparator.comparing(CombatGroupEntity::getExecutionTime))
                .filter(cG -> !(settlement.getId().equals(cG.getFromSettlementId()) &&
                        cG.getMission().equals(ECombatGroupMission.BACK)))
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
                        return new CombatGroupMovedView(cG.getId(), nation, cG.getMission(),true, null,
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
}
