package io.lanu.travian.game.services;

import io.lanu.travian.enums.EBuilding;
import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.errors.UserErrorException;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.events.*;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.*;
import io.lanu.travian.security.UserEntity;
import io.lanu.travian.security.UsersRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SettlementStateImpl implements SettlementState {

    private static final MathContext mc = new MathContext(3);
    
    private final SettlementService settlementService;
    private final ConstructionService constructionService;
    private final MilitaryService militaryService;
    private final UsersRepository usersRepository;

    public SettlementStateImpl(SettlementService settlementService, ConstructionService constructionService,
                               MilitaryService militaryService, UsersRepository usersRepository) {
        this.settlementService = settlementService;
        this.constructionService = constructionService;
        this.militaryService = militaryService;
        this.usersRepository = usersRepository;
    }

    @Override
    public SettlementEntity newVillage(NewVillageRequest newVillageRequest) {
        return settlementService.newVillage(newVillageRequest);
    }

    @Override
    public VillageView getVillageById(String villageId) {
        var villageEntity = recalculateCurrentState(villageId);
        var currentBuildingEvents = constructionService.findAllByVillageId(villageId);
        var militariesInVillage = militaryService.getAllByTargetVillageId(villageEntity.getId());
        saveState(villageEntity);
        return new VillageView(villageEntity, currentBuildingEvents, militariesInVillage);
    }

    @Override
    public void updateVillageName(String villageId, String name) {
        var village = settlementService.updateName(recalculateCurrentState(villageId), name);
        saveState(village);
    }

    @Override
    public List<NewBuilding> getListOfAllNewBuildings(String villageId) {
        return constructionService.getListOfAllNewBuildings(recalculateCurrentState(villageId));
    }

    @Override
    public void createBuildEvent(String villageId, Integer position, EBuilding kind) {
        var village = constructionService.createBuildEvent(recalculateCurrentState(villageId), position, kind);
        saveState(village);
    }

    @Override
    public void deleteBuildingEvent(String villageId, String eventId) {
        var village = constructionService.deleteBuildingEvent(recalculateCurrentState(villageId), eventId);
        saveState(village);
    }

    @Override
    public Map<String, List<MilitaryUnitView>> getAllMilitaryUnitsByVillage(String villageId) {
        return militaryService.getAllMilitaryUnitsByVillage(recalculateCurrentState(villageId));
    }

    @Override
    public void orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest) {
        var village = militaryService.orderCombatUnits(orderCombatUnitRequest, recalculateCurrentState(orderCombatUnitRequest.getVillageId()));
        saveState(village);
    }

    @Override
    public List<CombatUnitOrderResponse> getAllOrdersByVillageId(String villageId) {
        saveState(recalculateCurrentState(villageId));
        return militaryService.getAllOrdersByVillageId(villageId);
    }

    @Override
    public List<ECombatUnit> getAllResearchedUnits(String villageId) {
        saveState(recalculateCurrentState(villageId));
        return militaryService.getAllResearchedUnits(villageId);
    }

    @Override
    public MilitaryUnitContract checkTroopsSendingRequest(TroopsSendingRequest troopsSendingRequest) {
        var attackingVillage = recalculateCurrentState(troopsSendingRequest.getVillageId());
        var attackedVillageOpt = settlementService
                .findVillageByCoordinates(troopsSendingRequest.getX(), troopsSendingRequest.getY());
        if (attackedVillageOpt.isPresent()) {
            return militaryService.checkTroopsSendingRequest(troopsSendingRequest, attackingVillage, attackedVillageOpt.get());
        }else {
            throw new UserErrorException("There is nothing on those coordinates");
        }
    }

    @Override
    public void sendTroops(MilitaryUnitContract militaryUnitContract) {
        var village = recalculateCurrentState(militaryUnitContract.getOriginVillageId());
        village = militaryService.sendTroops(militaryUnitContract, village);
        saveState(village);
    }

    @Override
    public TileDetail getTileDetail(String id, int fromX, int fromY) {
        var village = recalculateCurrentState(id);
        var user = usersRepository.findByUserId(village.getAccountId()).orElse(new UserEntity("Nature"));
        return new TileDetail(village.getId(), village.getSettlementType(), village.getSubType(), village.getNation(), user.getUsername(),
                village.getName(), village.getX(), village.getY(), village.getPopulation(),
                MilitaryServiceImpl.getDistance(village.getX(), village.getY(), fromX, fromY));
    }

    public SettlementEntity recalculateCurrentState(String villageId) {
        SettlementEntity settlementEntity = settlementService.findById(villageId);
        var allEvents = combineAllEvents(settlementEntity);
        executeAllEvents(settlementEntity, allEvents);
        settlementEntity.castStorage();
        return saveState(settlementEntity);
    }

    private SettlementEntity saveState(SettlementEntity village){
        return settlementService.saveVillage(village);
    }

    private void executeAllEvents(SettlementEntity settlementEntity, List<EventStrategy> allEvents) {
        var executor = new EventExecutor();
        var modified = settlementEntity.getModified();
        for (EventStrategy eventStrategy : allEvents) {
            var cropPerHour = settlementEntity.calculateProducePerHour().get(EResource.CROP);

            // if crop in the village is less than 0 keep create the death event & execute them until the crop will be positive
            while (cropPerHour.longValue() < 0) {
                var leftCrop = settlementEntity.getStorage().get(EResource.CROP);
                var durationToDeath = leftCrop.divide(cropPerHour.negate(), mc).multiply(BigDecimal.valueOf(3_600_000), mc);

                LocalDateTime deathTime = modified.plus(durationToDeath.longValue(), ChronoUnit.MILLIS);

                if (deathTime.isBefore(eventStrategy.getExecutionTime())) {
                    EventStrategy deathBuildStrategy = new DeathEventStrategy(deathTime);
                    settlementEntity.calculateProducedGoods(modified, deathBuildStrategy.getExecutionTime());
                    executor.setStrategy(deathBuildStrategy);
                    executor.tryExecute();
                    modified = deathBuildStrategy.getExecutionTime();
                } else {
                    break;
                }
                cropPerHour = settlementEntity.calculateProducePerHour().get(EResource.CROP);
            }
            // recalculate storage leftovers
            settlementEntity.calculateProducedGoods(modified, eventStrategy.getExecutionTime());
            executor.setStrategy(eventStrategy);
            executor.tryExecute();
            modified = eventStrategy.getExecutionTime();
        }
    }

    private List<EventStrategy> combineAllEvents(SettlementEntity origin) {

        // add all building events
        List<EventStrategy> allEvents = constructionService.findAllByVillageId(origin.getId()).stream()
                .filter(event -> event.getExecutionTime().isBefore(LocalDateTime.now()))
                .map(cE -> new ConstructionEventStrategy(origin, cE))
                .collect(Collectors.toList());

        // add all units events
        var combatEventList = militaryService.createCombatUnitDoneEventsFromOrders(origin);
        allEvents.addAll(combatEventList);

        // add all wars events
        var militaryEventList = militaryService.getAllByOriginVillageIdOrTargetVillageId(origin.getId())
                .stream()
                .filter(militaryUnitEntity -> militaryUnitEntity.getExecutionTime().isBefore(LocalDateTime.now()))
                .map(mU -> new MilitaryEventStrategy(
                        origin, mU, new VillageBrief(mU.getTargetVillageId(), mU.getTarget().getVillageName(),
                        mU.getTarget().getPlayerName(), mU.getTarget().getCoordinates()), this, militaryService))
                .collect(Collectors.toList());
        allEvents.addAll(militaryEventList);

        // add last empty event
        allEvents.add(new LastEventStrategy(LocalDateTime.now()));

        return allEvents.stream()
                .sorted(Comparator.comparing(EventStrategy::getExecutionTime))
                .collect(Collectors.toList());
    }

}
