package io.lanu.travian.game.services;

import io.lanu.travian.enums.*;
import io.lanu.travian.game.entities.ArmyOrderEntity;
import io.lanu.travian.game.entities.ResearchedTroopsEntity;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.*;
import io.lanu.travian.game.models.ResearchedTroopShort;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.ShortVillageInfo;
import io.lanu.travian.game.models.responses.VillageView;
import io.lanu.travian.game.repositories.ArmyOrdersRepository;
import io.lanu.travian.game.repositories.ConstructionEventsRepository;
import io.lanu.travian.game.repositories.ResearchedTroopsRepository;
import io.lanu.travian.game.repositories.VillageRepository;
import io.lanu.travian.templates.villages.VillageEntityFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VillageServiceImpl implements VillageService{
    private final VillageRepository villageRepository;
    private final ArmyOrdersRepository armyOrdersRepository;
    private final ConstructionEventsRepository constructionEventsRepository;
    private final ResearchedTroopsRepository researchedTroopsRepository;
    private static final MathContext mc = new MathContext(3);

    public VillageServiceImpl(VillageRepository villageRepository,
                              ArmyOrdersRepository armyOrdersRepository,
                              ConstructionEventsRepository constructionEventsRepository, ResearchedTroopsRepository researchedTroopsRepository) {
        this.villageRepository = villageRepository;
        this.armyOrdersRepository = armyOrdersRepository;
        this.constructionEventsRepository = constructionEventsRepository;
        this.researchedTroopsRepository = researchedTroopsRepository;
    }

    @Override
    public VillageEntity createVillage(NewVillageRequest newVillageRequest) {
        VillageEntity newVillage = VillageEntityFactory.getVillageByType(EVillageType.SIX);
        Objects.requireNonNull(newVillage).setAccountId(newVillageRequest.getAccountId());
        newVillage.setX(newVillageRequest.getX());
        newVillage.setY(newVillageRequest.getY());
        var result = villageRepository.save(newVillage);
        researchedTroopsRepository.save(
                new ResearchedTroopsEntity(result.getVillageId(),
                        List.of(new ResearchedTroopShort(EUnits.PHALANX.getName(), 0))));
        return result;
    }

    @Override
    public List<ShortVillageInfo> getAllVillagesByUserId(String userId) {
        return villageRepository.findAllByAccountId(userId)
                .stream()
                .map(village -> new ShortVillageInfo(village.getVillageId(), village.getName(), village.getX(), village.getY()))
                .collect(Collectors.toList());
    }

    @Override
    public String updateName(String villageId, String newName) {
        VillageEntity villageEntity = this.villageRepository.findById(villageId)
                .orElseThrow(() -> new IllegalStateException(String.format("Village with id - %s is not exist.", villageId)));
        villageEntity.setName(newName);
        villageRepository.save(villageEntity);
        return newName;
    }

    @Override
    public VillageView getVillageById(String villageId) {
        var villageEntity = recalculateVillage(villageId);
        constructionEventsRepository.deleteAllByVillageIdAndExecutionTimeBefore(villageId, LocalDateTime.now());
        List<ConstructionEvent> currentBuildingEvents = constructionEventsRepository.findAllByVillageId(villageId);
        return new VillageView(villageEntity, currentBuildingEvents);
    }

    public VillageEntity saveVillage(VillageEntity villageEntity){
        return villageRepository.save(villageEntity);
    }

    public VillageEntity recalculateVillage(String villageId){

        VillageEntity villageEntity = this.villageRepository.findById(villageId)
                .orElseThrow(() -> new IllegalStateException(String.format("Village with id - %s is not exist.", villageId)));

        List<IEvent> allEvents = combineAllEvents(villageEntity);
        LocalDateTime modified = villageEntity.getModified();

        // iterate over all events and execute them
        for (IEvent event : allEvents) {
            var cropPerHour = villageEntity.calculateProducePerHour().get(EResource.CROP);

            // if crop in the village is less than 0 keep create the death event & execute them until the crop will be positive
            while (cropPerHour.longValue() < 0) {
                var leftCrop = villageEntity.getStorage().get(EResource.CROP);
                var durationToDeath = leftCrop.divide(cropPerHour.negate(), mc).multiply(BigDecimal.valueOf(3_600_000), mc);

                LocalDateTime deathTime = modified.plus(durationToDeath.longValue(), ChronoUnit.MILLIS);

                if (deathTime.isBefore(event.getExecutionTime())) {
                    IEvent deathBuildEvent = new DeathEvent(deathTime);
                    villageEntity.calculateProducedGoods(modified, deathBuildEvent.getExecutionTime());
                    deathBuildEvent.execute(villageEntity);
                    modified = deathBuildEvent.getExecutionTime();
                } else {
                    break;
                }
                cropPerHour = villageEntity.calculateProducePerHour().get(EResource.CROP);
            }
            // recalculate storage leftovers
            villageEntity.calculateProducedGoods(modified, event.getExecutionTime());
            event.execute(villageEntity);
            modified = event.getExecutionTime();
        }
        villageEntity.castStorage();
        return saveVillage(villageEntity);
    }

    private List<IEvent> combineAllEvents(VillageEntity villageEntity) {
        List<IEvent> allEvents = new ArrayList<>();

        // add all building events
        allEvents.addAll(constructionEventsRepository.findAllByVillageId(villageEntity.getVillageId())
                .stream()
                .filter(event -> event.getExecutionTime().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(ConstructionEvent::getExecutionTime))
                .collect(Collectors.toList()));

        // add all units events
        allEvents.addAll(createTroopsBuildEventsFromOrders(villageEntity.getVillageId()));

        allEvents.add(new LastEvent(LocalDateTime.now()));

        return allEvents.stream()
                .filter(task -> task.getExecutionTime().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(IEvent::getExecutionTime))
                .collect(Collectors.toList());
    }

    private List<TroopDoneEvent> createTroopsBuildEventsFromOrders(String villageId) {

        List<TroopDoneEvent> result = new ArrayList<>();
        List<ArmyOrderEntity> ordersList = armyOrdersRepository.findAllByVillageId(villageId);

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

    private List<TroopDoneEvent> addCompletedTroops(ArmyOrderEntity order, Integer amount) {
        List<TroopDoneEvent> result = new ArrayList<>();
        LocalDateTime exec = order.getLastTime();
        for (int i = 0; i < amount; i++) {
            exec = exec.plus(order.getDurationEach(), ChronoUnit.SECONDS);
            result.add(new TroopDoneEvent(exec, order.getUnitType(), order.getEatHour()));
        }
        return result;
    }
}
