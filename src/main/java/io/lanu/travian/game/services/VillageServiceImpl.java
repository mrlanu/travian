package io.lanu.travian.game.services;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.enums.EVillageType;
import io.lanu.travian.game.entities.ResearchedCombatUnitEntity;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.entities.events.*;
import io.lanu.travian.game.models.ResearchedCombatUnitShort;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.ShortVillageInfo;
import io.lanu.travian.game.models.responses.VillageView;
import io.lanu.travian.game.repositories.ResearchedCombatUnitRepository;
import io.lanu.travian.game.repositories.VillageRepository;
import io.lanu.travian.templates.villages.VillageEntityFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VillageServiceImpl implements VillageService{
    private final VillageRepository villageRepository;
    private final ResearchedCombatUnitRepository researchedCombatUnitRepository;
    private final MilitaryService militaryService;
    private final IConstructionService constructionService;
    private static final MathContext mc = new MathContext(3);

    public VillageServiceImpl(VillageRepository villageRepository, ResearchedCombatUnitRepository researchedCombatUnitRepository,
                              MilitaryService militaryService, IConstructionService constructionService) {
        this.villageRepository = villageRepository;
        this.researchedCombatUnitRepository = researchedCombatUnitRepository;
        this.militaryService = militaryService;
        this.constructionService = constructionService;
    }

    @Override
    public VillageEntity createVillage(NewVillageRequest newVillageRequest) {
        var result = villageRepository.save(instantiateNewVillage(newVillageRequest));
        createResearchedCombatUnitEntity(result.getVillageId());
        return result;
    }

    private VillageEntity instantiateNewVillage(NewVillageRequest newVillageRequest){
        VillageEntity newVillage = VillageEntityFactory.getVillageByType(EVillageType.SIX);
        Objects.requireNonNull(newVillage).setAccountId(newVillageRequest.getAccountId());
        newVillage.setX(newVillageRequest.getX());
        newVillage.setY(newVillageRequest.getY());
        return newVillage;
    }

    private void createResearchedCombatUnitEntity(String villageId) {
        researchedCombatUnitRepository.save(
                new ResearchedCombatUnitEntity(villageId,
                        List.of(new ResearchedCombatUnitShort(ECombatUnit.PHALANX.getName(), 0))));
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
    public Optional<VillageEntity> findVillageByCoordinates(int x, int y) {
        return villageRepository.findByXAndY(x, y);
    }

    @Override
    public VillageEntity saveVillage(VillageEntity villageEntity){
        return villageRepository.save(villageEntity);
    }

    @Override
    public VillageView getVillageById(String villageId) {
        var villageEntity = recalculateVillage(villageId);
        constructionService.deleteAllByVillageIdAndExecutionTimeBefore(villageId, LocalDateTime.now());
        List<ConstructionEvent> currentBuildingEvents = constructionService.findAllByVillageId(villageId);
        return new VillageView(villageEntity, currentBuildingEvents);
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
            if (event instanceof MilitaryEvent){
                MilitaryEvent militaryEvent = (MilitaryEvent) event;
                var targetVillage = recalculateVillage(militaryEvent.getTargetVillageId());
                militaryEvent.execute(targetVillage);
            }else {
                event.execute(villageEntity);
            }
            modified = event.getExecutionTime();
        }
        villageEntity.castStorage();
        return saveVillage(villageEntity);
    }

    private List<IEvent> combineAllEvents(VillageEntity villageEntity) {
        List<IEvent> allEvents = new ArrayList<>();

        // add all building events
        allEvents.addAll(constructionService.findAllByVillageId(villageEntity.getVillageId()));

        // add all units events
        allEvents.addAll(militaryService.createCombatUnitDoneEventsFromOrders(villageEntity.getVillageId()));

        // add all wars events
        allEvents.addAll(militaryService.getAllByOriginVillageId(villageEntity.getVillageId()));

        // add last empty event
        allEvents.add(new LastEvent(LocalDateTime.now()));

        return allEvents.stream()
                .filter(event -> event.getExecutionTime().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(IEvent::getExecutionTime))
                .collect(Collectors.toList());
    }
}
