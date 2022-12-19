package io.lanu.travian.game.services;

import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.CombatUnitDoneEventEntity;
import io.lanu.travian.game.models.event.*;
import io.lanu.travian.game.repositories.CombatGroupRepository;
import io.lanu.travian.game.repositories.ReportRepository;
import io.lanu.travian.game.repositories.SettlementRepository;
import io.lanu.travian.game.repositories.StatisticsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SettlementStateImpl implements SettlementState {

    private static final MathContext mc = new MathContext(3);
    private final SettlementRepository settlementRepository;
    private final CombatGroupRepository combatGroupRepository;
    private final ReportRepository reportRepository;
    private final StatisticsRepository statisticsRepository;

    public SettlementStateImpl(SettlementRepository settlementRepository, CombatGroupRepository combatGroupRepository,
                               ReportRepository reportRepository, StatisticsRepository statisticsRepository) {
        this.settlementRepository = settlementRepository;
        this.combatGroupRepository = combatGroupRepository;
        this.reportRepository = reportRepository;
        this.statisticsRepository = statisticsRepository;
    }

    @Override
    public SettlementEntity save(SettlementEntity settlement){
        return settlementRepository.save(settlement);
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

    public SettlementEntity recalculateCurrentState(String villageId) {
        SettlementEntity settlementEntity = settlementRepository.findById(villageId).orElseThrow();
        var allEvents = combineAllEvents(settlementEntity);
        executeAllEvents(settlementEntity, allEvents);
        settlementEntity.castStorage();
        return settlementRepository.save(settlementEntity);
    }

    private void executeAllEvents(SettlementEntity settlementEntity, List<Event> allEvents) {
        //var executor = new EventExecutor();
        var modified = settlementEntity.getModified();
        for (Event event : allEvents) {
            var cropPerHour = settlementEntity.calculateProducePerHour().get(EResource.CROP);

            // if crop in the village is less than 0 keep create the death event & execute them until the crop will be positive
            while (cropPerHour.longValue() < 0) {
                var leftCrop = settlementEntity.getStorage().get(EResource.CROP);
                var durationToDeath = leftCrop.divide(cropPerHour.negate(), mc).multiply(BigDecimal.valueOf(3_600_000), mc);

                LocalDateTime deathTime = modified.plus(durationToDeath.longValue(), ChronoUnit.MILLIS);

                if (deathTime.isBefore(event.getExecutionTime())) {
                    Event deathEvent = new DeathEvent(deathTime);
                    settlementEntity.calculateProducedGoods(modified, deathEvent.getExecutionTime());
                    deathEvent.execute(settlementEntity);
                    modified = deathEvent.getExecutionTime();
                } else {
                    break;
                }
                cropPerHour = settlementEntity.calculateProducePerHour().get(EResource.CROP);
            }
            // recalculate storage leftovers
            settlementEntity.calculateProducedGoods(modified, event.getExecutionTime());
            event.execute(settlementEntity);
            modified = event.getExecutionTime();
        }
    }

    private List<Event> combineAllEvents(SettlementEntity currentSettlement) {

        // add all building events
        List<Event> allEvents = currentSettlement.getConstructionEventList().stream()
                .filter(event -> event.getExecutionTime().isBefore(LocalDateTime.now()))
                .map(event -> new ConstructionEvent(event, statisticsRepository))
                .collect(Collectors.toList());

        // add all units events
        var combatEventList = createCombatUnitDoneEventsFromOrders(currentSettlement);
        allEvents.addAll(combatEventList);


        // add all wars events
        var militaryEventList = combatGroupRepository
                .getCombatGroupByOwnerSettlementIdOrToSettlementId(currentSettlement.getId(), currentSettlement.getId())
                .stream()
                .filter(cG -> cG.isMoved() && cG.getExecutionTime().isBefore(LocalDateTime.now()))
                .map(cG -> new TroopsArrivedEvent(cG, this))
                .collect(Collectors.toList());

        allEvents.addAll(militaryEventList);

        // add last empty event
        allEvents.add(new LastEvent(LocalDateTime.now()));

        return allEvents.stream()
                .sorted(Comparator.comparing(Event::getExecutionTime))
                .collect(Collectors.toList());
    }

    private List<Event> createCombatUnitDoneEventsFromOrders(SettlementEntity settlement) {

        List<Event> result = new ArrayList<>();
        var ordersList = settlement.getCombatUnitOrders();
        List<OrderCombatUnitEntity> newOrdersList = new ArrayList<>();

        if (ordersList.size() > 0) {
            for (OrderCombatUnitEntity order : ordersList) {
                long duration = Duration.between(order.getLastTime(), LocalDateTime.now()).toSeconds();

                if (LocalDateTime.now().isAfter(order.getEndOrderTime())) {
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
            result.add(new CombatUnitDoneEvent(new CombatUnitDoneEventEntity(exec, order.getUnitType(), order.getEatHour())));
        }
        return result;
    }
}
