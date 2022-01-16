package io.lanu.travian.game.services;

import io.lanu.travian.enums.*;
import io.lanu.travian.errors.UserErrorException;
import io.lanu.travian.game.entities.events.MilitaryEvent;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.*;
import io.lanu.travian.game.repositories.CombatUnitOrderRepository;
import io.lanu.travian.game.repositories.IMilitaryUnitRepository;
import io.lanu.travian.game.repositories.ResearchedCombatUnitRepository;
import io.lanu.travian.security.UserEntity;
import io.lanu.travian.security.UsersRepository;
import io.lanu.travian.templates.military.CombatUnitFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MilitaryServiceImpl implements MilitaryService {

    private final CombatUnitOrderRepository combatUnitOrderRepository;
    private final ResearchedCombatUnitRepository researchedCombatUnitRepository;
    private final VillageService villageService;
    private final IMilitaryUnitRepository militaryUnitRepository;
    private final UsersRepository usersRepository;

    public MilitaryServiceImpl(CombatUnitOrderRepository combatUnitOrderRepository,
                               ResearchedCombatUnitRepository researchedCombatUnitRepository,
                               VillageService villageService, IMilitaryUnitRepository militaryUnitRepository, UsersRepository usersRepository) {
        this.combatUnitOrderRepository = combatUnitOrderRepository;
        this.researchedCombatUnitRepository = researchedCombatUnitRepository;
        this.villageService = villageService;
        this.militaryUnitRepository = militaryUnitRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public Map<String, List<MilitaryUnit>> getAllMilitaryUnitsByVillageId(String villageId) {
        var village = villageService.recalculateVillage(villageId);
        var userName = usersRepository.findByUserId(village.getAccountId()).orElseThrow();

        // other units
        List<MilitaryUnit> unitsList = militaryUnitRepository.getAllByOriginVillageIdOrTargetVillageId(villageId, villageId)
                .stream()
                .peek(militaryEvent -> {
                    if (militaryEvent.getOriginVillageId().equals(villageId)){
                        militaryEvent.setState(EMilitaryUnitState.OUT);
                    }else {
                        militaryEvent.setState(EMilitaryUnitState.IN);
                    }
                })
                .map(mEv -> new MilitaryUnitDynamic(mEv.getId(), mEv.getNation(), true, mEv.getState(),
                                new VillageBrief(mEv.getOriginVillageId(), mEv.getOriginVillageName(), mEv.getOriginPlayerName(), mEv.getOriginVillageCoordinates()),
                                mEv.getUnits(), mEv.getMission(),
                                new VillageBrief(mEv.getTargetVillageId(), mEv.getTargetVillageName(), mEv.getTargetPlayerName(), mEv.getTargetVillageCoordinates()),
                                mEv.getExecutionTime(), mEv.getDuration()))
                .collect(Collectors.toList());

        Map<String, List<MilitaryUnit>> militaryUnitsMap = unitsList.stream()
                .collect(Collectors.groupingBy(militaryEvent -> militaryEvent.getState().getName()));

        // home army
        MilitaryUnit homeArmy = new MilitaryUnitStatic("home", village.getNation(), false, EMilitaryUnitState.HOME,
                new VillageBrief(villageId, village.getName(), userName.getUsername(), new int[]{village.getX(), village.getY()}),
                village.getHomeLegion(), villageId, 5);

        militaryUnitsMap.put(EMilitaryUnitState.HOME.getName(), List.of(homeArmy));
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
    public OrderCombatUnitEntity orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest) {

        VillageEntity villageEntity = villageService.recalculateVillage(orderCombatUnitRequest.getVillageId());
        ECombatUnit unit = orderCombatUnitRequest.getUnitType();
        List<OrderCombatUnitEntity> ordersList = getAllOrdersByVillageId(orderCombatUnitRequest.getVillageId());

        LocalDateTime lastTime = ordersList.size() > 0 ? ordersList.get(ordersList.size() - 1).getEndOrderTime() : LocalDateTime.now();

        LocalDateTime endOrderTime = lastTime.plus(
                orderCombatUnitRequest.getAmount() * unit.getTime(), ChronoUnit.SECONDS);

        OrderCombatUnitEntity armyOrder = new OrderCombatUnitEntity(orderCombatUnitRequest.getVillageId(), lastTime, orderCombatUnitRequest.getUnitType(),
                orderCombatUnitRequest.getAmount(), unit.getTime(), unit.getEat(), endOrderTime);

        spendResources(orderCombatUnitRequest.getAmount(), villageEntity, unit);

        villageService.saveVillage(villageEntity);
        return combatUnitOrderRepository.save(armyOrder);
    }

    private void spendResources(int unitsAmount, VillageEntity villageEntity, ECombatUnit kind) {
        Map<EResource, BigDecimal> neededResources = new HashMap<>();
        kind.getCost().forEach((k, v) -> neededResources.put(k, BigDecimal.valueOf((long) v * unitsAmount)));
        villageEntity.manipulateGoods(EManipulation.SUBTRACT, neededResources);
    }

    @Override
    public List<OrderCombatUnitEntity> getAllOrdersByVillageId(String villageId){
        return combatUnitOrderRepository
                .findAllByVillageId(villageId)
                .stream()
                .sorted(Comparator.comparing(OrderCombatUnitEntity::getCreated))
                .collect(Collectors.toList());
    }

    @Override
    public MilitaryUnitContract checkTroopsSendingRequest(TroopsSendingRequest troopsSendingRequest) {
        var attackingVillage = villageService.recalculateVillage(troopsSendingRequest.getVillageId());
        var attackingUser = usersRepository.findByUserId(attackingVillage.getAccountId()).orElseThrow();
        VillageEntity attackedVillage;
        UserEntity attackedUser;
        var attackedVillageOpt = villageService
                .findVillageByCoordinates(troopsSendingRequest.getX(), troopsSendingRequest.getY());
        if (attackedVillageOpt.isPresent()){
            attackedVillage = villageService.recalculateVillage(attackedVillageOpt.get().getVillageId());
            attackedUser = usersRepository.findByUserId(attackedVillage.getAccountId()).orElseThrow();
        } else {
            throw new UserErrorException("There is nothing on those coordinates");
        }

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
                .arrivalTime(LocalDateTime.now().plusMinutes(2))
                .duration(120)
                .build();
    }

    @Override
    public boolean sendTroops(MilitaryUnitContract contract) {
        // deduct all involved units from village army
        var attackingVillage = villageService.recalculateVillage(contract.getOriginVillageId());
        var homeLegion = attackingVillage.getHomeLegion();
        var attackingUnits = contract.getUnits();
        for (int i = 0; i < homeLegion.length; i++){
            homeLegion[i] = homeLegion[i] - attackingUnits[i];
        }
        villageService.saveVillage(attackingVillage);
        // create MilitaryUnitEntity
        var militaryUnitEntity = new MilitaryEvent(contract.getNation(), true, EMilitaryUnitState.OUT, contract.getMission(),
                contract.getOriginVillageId(), contract.getOriginVillageName(), contract.getOriginPlayerName(),
                contract.getOriginVillageCoordinates(), contract.getTargetVillageId(), contract.getTargetVillageName(),
                contract.getTargetPlayerName(), contract.getTargetVillageCoordinates(), null,
                contract.getArrivalTime(), 120, 0, contract.getUnits());
        militaryUnitRepository.save(militaryUnitEntity);
        return true;
    }
}
