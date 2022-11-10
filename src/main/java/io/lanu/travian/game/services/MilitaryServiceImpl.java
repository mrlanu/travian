package io.lanu.travian.game.services;

import io.lanu.travian.Consts;
import io.lanu.travian.enums.*;
import io.lanu.travian.errors.UserErrorException;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.entities.events.MilitaryUnitEntity;
import io.lanu.travian.game.entities.events.MovedMilitaryUnitEntity;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.requests.TroopsSendingRequest;
import io.lanu.travian.game.models.responses.*;
import io.lanu.travian.game.repositories.CombatUnitOrderRepository;
import io.lanu.travian.game.repositories.MilitaryUnitRepository;
import io.lanu.travian.game.repositories.MovedMilitaryUnitRepository;
import io.lanu.travian.game.repositories.ResearchedCombatUnitRepository;
import io.lanu.travian.security.UserEntity;
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
    private final MilitaryUnitRepository militaryUnitRepository;
    private final MovedMilitaryUnitRepository movedMilitaryUnitRepository;
    private final UsersRepository usersRepository;

    private final SettlementRepository settlementRepository;

    public MilitaryServiceImpl(CombatUnitOrderRepository combatUnitOrderRepository,
                               ResearchedCombatUnitRepository researchedCombatUnitRepository,
                               MilitaryUnitRepository militaryUnitRepository, MovedMilitaryUnitRepository movedMilitaryUnitRepository, UsersRepository usersRepository, SettlementRepository settlementRepository) {
        this.combatUnitOrderRepository = combatUnitOrderRepository;
        this.researchedCombatUnitRepository = researchedCombatUnitRepository;
        this.militaryUnitRepository = militaryUnitRepository;
        this.movedMilitaryUnitRepository = movedMilitaryUnitRepository;
        this.usersRepository = usersRepository;
        this.settlementRepository = settlementRepository;
    }

    @Override
    public MilitaryUnitEntity saveMilitaryUnit(MilitaryUnitEntity unit) {
        return militaryUnitRepository.save(unit);
    }

    @Override
    public MovedMilitaryUnitEntity saveMovedMilitaryUnit(MovedMilitaryUnitEntity unit) {
        return movedMilitaryUnitRepository.save(unit);
    }

    @Override
    public void deleteMovedUnitById(String id) {
        movedMilitaryUnitRepository.deleteById(id);
    }

    /*@Override
    public void deleteUnitById(String id) {
        militaryUnitRepository.deleteById(id);
    }

    @Override
    public List<MilitaryUnitEntity> getAllByTargetVillageId(String villageId) {
        return militaryUnitRepository.getAllByTargetVillageId(villageId);
    }

    @Override
    public List<MovedMilitaryUnitEntity> getAllMovedUnitsByOriginVillageId(String villageId) {
        return movedMilitaryUnitRepository.getAllByOriginVillageId(villageId);
    }
*/
    @Override
    public Map<String, List<MilitaryUnitView>> getAllMilitaryUnitsByVillage(SettlementEntity village) {
        var userName = usersRepository.findByUserId(village.getAccountId()).orElseThrow();
        var villageId = village.getId();
        // other units
        List<MilitaryUnitView> unitsList = movedMilitaryUnitRepository.getAllByOriginVillageIdOrTargetVillageId(villageId, villageId)
                .stream()
                .map(mEv -> new MovedMilitaryUnitView(mEv.getId(), mEv.getNation(), mEv.getMission(), true, null,
                                new VillageBrief(mEv.getOriginVillageId(), mEv.getOrigin().getVillageName(),
                                        mEv.getOrigin().getPlayerName(), mEv.getOrigin().getCoordinates()),
                                new VillageBrief(mEv.getTargetVillageId(), mEv.getTarget().getVillageName(),
                                mEv.getTarget().getPlayerName(), mEv.getTarget().getCoordinates()),
                                mEv.getUnits(), mEv.getPlunder(), mEv.getExecutionTime(), (int) Duration.between(LocalDateTime.now(),
                                mEv.getExecutionTime()).toSeconds()))
                .peek(mU -> {
                    if (mU.getOrigin().getVillageId().equals(villageId)){
                        mU.setState(EMilitaryUnitLocation.OUT);
                    }else {
                        mU.setState(EMilitaryUnitLocation.IN);
                    }
                })
                .collect(Collectors.toList());

        unitsList.addAll(
                militaryUnitRepository.getAllByOriginVillageIdOrTargetVillageId(villageId, villageId).stream()
                    .map(mEv -> new MilitaryUnitViewStatic(mEv.getId(), mEv.getNation(), mEv.getMission(), false, null,
                            new VillageBrief(mEv.getOriginVillageId(), mEv.getOrigin().getVillageName(),
                                    mEv.getOrigin().getPlayerName(), mEv.getOrigin().getCoordinates()),
                            new VillageBrief(mEv.getTargetVillageId(), mEv.getTarget().getVillageName(),
                                    mEv.getTarget().getPlayerName(), mEv.getTarget().getCoordinates()),
                            mEv.getUnits(), mEv.getTargetVillageId(), mEv.getEatExpenses()))
                        .peek(mU -> {
                            if (mU.getOrigin().getVillageId().equals(villageId)){
                                mU.setState(EMilitaryUnitLocation.AWAY);
                            }else {
                                mU.setState(EMilitaryUnitLocation.HOME);
                            }
                        })
                        .collect(Collectors.toList()));

        Map<String, List<MilitaryUnitView>> militaryUnitsMap = unitsList.stream()
                .collect(Collectors.groupingBy(militaryEvent -> militaryEvent.getState().getName()));

        // home army
        MilitaryUnitView homeArmy = new MilitaryUnitViewStatic("home", village.getNation(), EMilitaryUnitMission.HOME.getName(),
                false, EMilitaryUnitLocation.HOME,
                new VillageBrief(villageId, village.getName(), userName.getUsername(), new int[]{village.getX(), village.getY()}),
                new VillageBrief(villageId, village.getName(), userName.getUsername(), new int[]{village.getX(), village.getY()}),
                village.getHomeLegion(), villageId, 5);

        var homeArmies = militaryUnitsMap.getOrDefault(EMilitaryUnitLocation.HOME.getName(), new ArrayList<>());
        homeArmies.add(homeArmy);
        militaryUnitsMap.put(EMilitaryUnitLocation.HOME.getName(), homeArmies);
        return militaryUnitsMap;
    }

    @Override
    public List<TroopMovementsResponse> getTroopMovements(SettlementEntity settlement) {
        List<TroopMovementsResponse> result = new ArrayList<>();
        //sort outgoing (true) & incoming (false)
        var movedUnits = movedMilitaryUnitRepository
                .getAllByOriginVillageIdOrTargetVillageId(settlement.getId(), settlement.getId())
                .stream()
                .sorted(Comparator.comparing(MovedMilitaryUnitEntity::getExecutionTime))
                .collect(Collectors.partitioningBy(m -> m.getOriginVillageId().equals(settlement.getId()),
                        // sort attacks (true) & reinforcements (false)
                        Collectors.groupingBy(m -> m.getMission().equals(EMilitaryUnitMission.ATTACK.getName()) ||
                                m.getMission().equals(EMilitaryUnitMission.RAID.getName()))));

        // outgoing
        // attacks & raids
        if (movedUnits.get(true).getOrDefault(true, new ArrayList<>()).size() > 0) {
            result.add(new TroopMovementsResponse(movedUnits.get(true).get(true).size(), EMilitaryUnitMission.ATTACK.getName(),
                    (int) Duration.between(LocalDateTime.now(), movedUnits.get(true).get(true).get(0).getExecutionTime()).toSeconds()));
        } else {
            result.add(new TroopMovementsResponse());
        }
        //reinforcements
        if (movedUnits.get(true).getOrDefault(false, new ArrayList<>()).size() > 0){
            result.add(new TroopMovementsResponse(movedUnits.get(true).get(false).size(), EMilitaryUnitMission.REINFORCEMENT.getName(),
                    (int) Duration.between(LocalDateTime.now(), movedUnits.get(true).get(false).get(0).getExecutionTime()).toSeconds()));
        } else {
            result.add(new TroopMovementsResponse());
        }

        //incoming
        // attacks & raids
        if (movedUnits.get(false).getOrDefault(true, new ArrayList<>()).size() > 0) {
            result.add(new TroopMovementsResponse(movedUnits.get(false).get(true).size(), EMilitaryUnitMission.ATTACK.getName(),
                    (int) Duration.between(LocalDateTime.now(), movedUnits.get(false).get(true).get(0).getExecutionTime()).toSeconds()));
        } else {
            result.add(new TroopMovementsResponse());
        }

    //reinforcements
        if (movedUnits.get(false).getOrDefault(false, new ArrayList<>()).size() > 0){
            result.add(new TroopMovementsResponse(movedUnits.get(false).get(false).size(), EMilitaryUnitMission.REINFORCEMENT.getName(),
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

        var attackingUser = usersRepository.findByUserId(attackingVillage.getAccountId()).orElseThrow();
        UserEntity attackedUser;
        if (attackedVillage.getAccountId() != null){
            attackedUser = usersRepository.findByUserId(attackedVillage.getAccountId()).orElseThrow();
        } else {
            attackedUser = new UserEntity(null, null, "Nature", null);
        }
        var duration = getDistance(attackedVillage.getX(), attackedVillage.getY(), attackingVillage.getX(), attackingVillage.getY())
                .multiply(BigDecimal.valueOf(3600)
                        .divide(BigDecimal.valueOf(10 * Consts.SPEED), MathContext.DECIMAL32)).intValue();
        var arrivalTime = LocalDateTime.now().plusSeconds(duration);
        return MilitaryUnitContract.builder()
                .nation(attackingVillage.getNation())
                .mission(troopsSendingRequest.getKind().getName())
                .originVillageId(attackingVillage.getId())
                .originVillageName(attackingVillage.getName())
                .originPlayerName(attackingUser.getUsername())
                .originVillageCoordinates(new int[]{attackingVillage.getX(), attackingVillage.getY()})
                .targetVillageId(attackedVillage.getId())
                .targetVillageName(attackedVillage.getName())
                .targetPlayerName(attackedUser.getUsername())
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
        // create MilitaryUnitEntity
        var moveUnit = new MovedMilitaryUnitEntity(
                contract.getNation(), contract.getMission(), contract.getUnits(), null, contract.getOriginVillageId(),
                new VillageBrief(contract.getOriginVillageId(), contract.getOriginVillageName(), contract.getOriginPlayerName(),
                        contract.getOriginVillageCoordinates()), contract.getTargetVillageId(),
                new VillageBrief(contract.getTargetVillageName(), contract.getTargetPlayerName(),
                        contract.getTargetVillageCoordinates()),
                LocalDateTime.now().plusSeconds(contract.getDuration()), contract.getDuration(), 0);
        movedMilitaryUnitRepository.save(moveUnit);
        return village;
    }

    /*@Override
    public List<MovedMilitaryUnitEntity> getAllByOriginVillageIdOrTargetVillageId(String originId) {
        return movedMilitaryUnitRepository.getAllByOriginVillageIdOrTargetVillageId(originId, originId);
    }*/
}
