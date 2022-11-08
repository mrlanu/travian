package io.lanu.travian.game.services;

import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.CombatUnitDoneEventEntity;
import io.lanu.travian.game.models.event.*;
import io.lanu.travian.game.models.responses.VillageBrief;
import io.lanu.travian.game.repositories.CombatUnitOrderRepository;
import io.lanu.travian.game.repositories.ConstructionEventRepository;
import io.lanu.travian.game.repositories.MilitaryUnitRepository;
import io.lanu.travian.game.repositories.MovedMilitaryUnitRepository;
import io.lanu.travian.security.UsersRepository;
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

    private final ConstructionEventRepository constructionEventRepository;

    private final MovedMilitaryUnitRepository movedMilitaryUnitRepository;
    private final MilitaryUnitRepository militaryUnitRepository;

    private final CombatUnitOrderRepository combatUnitOrderRepository;

    public SettlementStateImpl(SettlementRepository settlementRepository, ConstructionEventRepository constructionEventRepository,
                               MovedMilitaryUnitRepository movedMilitaryUnitRepository, MilitaryUnitRepository militaryUnitRepository,
                               CombatUnitOrderRepository combatUnitOrderRepository) {
        this.settlementRepository = settlementRepository;
        this.constructionEventRepository = constructionEventRepository;
        this.movedMilitaryUnitRepository = movedMilitaryUnitRepository;
        this.militaryUnitRepository = militaryUnitRepository;
        this.combatUnitOrderRepository = combatUnitOrderRepository;
    }

    @Override
    public SettlementEntity save(SettlementEntity settlement){
        return settlementRepository.saveVillage(settlement);
    }

    @Override
    public MovedMilitaryUnitRepository getMovedMilitaryUnitRepository() {
        return movedMilitaryUnitRepository;
    }

    @Override
    public MilitaryUnitRepository getMilitaryUnitRepository() {
        return militaryUnitRepository;
    }

    public SettlementEntity recalculateCurrentState(String villageId) {
        SettlementEntity settlementEntity = settlementRepository.findById(villageId);
        var allEvents = combineAllEvents(settlementEntity);
        executeAllEvents(settlementEntity, allEvents);
        settlementEntity.castStorage();
        return settlementRepository.saveVillage(settlementEntity);
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
        List<Event> allEvents = constructionEventRepository.findAllByVillageId(currentSettlement.getId()).stream()
                .filter(event -> event.getExecutionTime().isBefore(LocalDateTime.now()))
                .map(ConstructionEvent::new)
                .collect(Collectors.toList());
        constructionEventRepository.deleteAllByVillageIdAndExecutionTimeBefore(currentSettlement.getId(), LocalDateTime.now());

        // add all units events
        var combatEventList = createCombatUnitDoneEventsFromOrders(currentSettlement.getId());
        allEvents.addAll(combatEventList);


        // add all wars events
        var militaryEventList = movedMilitaryUnitRepository
                .getAllByOriginVillageIdOrTargetVillageId(currentSettlement.getId(), currentSettlement.getId())
                .stream()
                .filter(militaryUnitEntity -> militaryUnitEntity.getExecutionTime().isBefore(LocalDateTime.now()))
                .map(mU -> new TroopsArrivedEvent(
                        mU,
                        new VillageBrief(mU.getTargetVillageId(), mU.getTarget().getVillageName(), mU.getTarget().getPlayerName(),
                                mU.getTarget().getCoordinates()), this))
                .collect(Collectors.toList());

        allEvents.addAll(militaryEventList);

        // add last empty event
        allEvents.add(new LastEvent(LocalDateTime.now()));

        return allEvents.stream()
                .sorted(Comparator.comparing(Event::getExecutionTime))
                .collect(Collectors.toList());
    }

    private List<Event> createCombatUnitDoneEventsFromOrders(String settlementId) {

        List<Event> result = new ArrayList<>();
        var ordersList = combatUnitOrderRepository.findAllByVillageId(settlementId);

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
