package io.lanu.travian.game.services;

import io.lanu.travian.Consts;
import io.lanu.travian.enums.EManipulation;
import io.lanu.travian.enums.ENation;
import io.lanu.travian.game.dto.SettlementStateDTO;
import io.lanu.travian.game.entities.CombatGroupContractEntity;
import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.entities.OrderCombatUnitEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.battle.Unit;
import io.lanu.travian.game.models.battle.UnitsConst;
import io.lanu.travian.game.models.requests.CombatGroupSendingRequest;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.responses.CombatGroupContractResponse;
import io.lanu.travian.game.models.responses.CombatUnitResponse;
import io.lanu.travian.game.repositories.CombatGroupContractRepository;
import io.lanu.travian.game.repositories.CombatGroupRepository;
import io.lanu.travian.game.repositories.ResearchedCombatUnitRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MilitaryServiceImpl implements MilitaryService {
    private final ResearchedCombatUnitRepository researchedCombatUnitRepository;
    private final CombatGroupRepository combatGroupRepository;
    private final CombatGroupContractRepository combatGroupContractRepository;
    private final EngineService engineService;

    public MilitaryServiceImpl(ResearchedCombatUnitRepository researchedCombatUnitRepository,
                               CombatGroupRepository combatGroupRepository,
                               CombatGroupContractRepository combatGroupContractRepository,
                               EngineService engineService) {
        this.researchedCombatUnitRepository = researchedCombatUnitRepository;
        this.combatGroupRepository = combatGroupRepository;
        this.combatGroupContractRepository = combatGroupContractRepository;
        this.engineService = engineService;
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
                .save(new CombatGroupContractEntity(null, settlementState.getId(), targetState.getAccountId(), combatGroupSendingRequest.getMission(),
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
    public List<CombatUnitResponse> getAllResearchedUnits(String villageId, ENation nation) {
        return researchedCombatUnitRepository.findByVillageId(villageId).getUnits()
                .stream()
                .map(shortUnit -> getUnit(UnitsConst.UNITS.get(nation.ordinal()).get(shortUnit.getUnit()), shortUnit.getLevel()))
                .collect(Collectors.toList());
    }

    private CombatUnitResponse getUnit(Unit unit, int level) {
        return CombatUnitResponse.builder()
                .name(unit.getName())
                .level(level)
                .attack(unit.getOffense())
                .defInfantry(unit.getDefenseInfantry())
                .defCavalry(unit.getDefenseCavalry())
                .speed(unit.getVelocity())
                .capacity(unit.getCapacity())
                .cost(unit.getCost())
                .eat(unit.getUpKeep())
                .time(unit.getTime() / Consts.SPEED)
                .description(unit.getDescription())
                .build();
    }

    @Override
    public SettlementStateDTO orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest, String settlementId) {
        var currentState = engineService.updateParticularSettlementState(settlementId, LocalDateTime.now());

        var unit = UnitsConst.UNITS
                .get(currentState.getSettlementEntity().getNation().ordinal())
                .get(orderCombatUnitRequest.getUnit());

        List<OrderCombatUnitEntity> ordersList = currentState.getSettlementEntity().getCombatUnitOrders()
                .stream()
                .sorted(Comparator.comparing(OrderCombatUnitEntity::getCreated))
                .collect(Collectors.toList());

        LocalDateTime lastTime = ordersList.size() > 0 ? ordersList.get(ordersList.size() - 1).getEndOrderTime() : LocalDateTime.now();

        LocalDateTime endOrderTime = lastTime.plus(
                orderCombatUnitRequest.getAmount() * (unit.getTime() / Consts.SPEED), ChronoUnit.SECONDS);

        OrderCombatUnitEntity armyOrder = new OrderCombatUnitEntity(orderCombatUnitRequest.getVillageId(), lastTime,
                orderCombatUnitRequest.getUnit(), orderCombatUnitRequest.getAmount(), (long)(unit.getTime() / Consts.SPEED), unit.getUpKeep(),
                endOrderTime);

        spendResources(orderCombatUnitRequest.getAmount(), currentState.getSettlementEntity(), unit);

        armyOrder.setCreated(LocalDateTime.now());
        ordersList.add(armyOrder);
        currentState.getSettlementEntity().setCombatUnitOrders(ordersList);

        return engineService.saveSettlementEntity(currentState);
    }

    private void spendResources(int unitsAmount, SettlementEntity settlementEntity, Unit unit) {
        var neededResources = unit.getCost().stream()
                .map(res -> BigDecimal.valueOf((long) res * unitsAmount))
                .collect(Collectors.toList());
        settlementEntity.manipulateGoods(EManipulation.SUBTRACT, neededResources);
    }

    public static BigDecimal getDistance(int x, int y, int fromX, int fromY) {
        var legX = BigDecimal.valueOf(x - fromX).pow(2);
        var legY = BigDecimal.valueOf(y - fromY).pow(2);
        return legX.add(legY).sqrt(new MathContext(2));
    }

    @Override
    public SettlementStateDTO sendTroops(SettlementStateDTO settlementState, String contractId) {
        // deduct all involved units from village army
        var homeLegion = settlementState.getSettlementEntity().getHomeLegion();
        var contractEntity = combatGroupContractRepository.findById(contractId).orElseThrow();
        var attackingUnits = contractEntity.getUnits();
        for (int i = 0; i < homeLegion.length; i++) {
            homeLegion[i] = homeLegion[i] - attackingUnits[i];
        }

        var combatGroup = CombatGroupEntity.builder()
                .moved(true)
                .ownerNation(settlementState.getSettlementEntity().getNation())
                .fromAccountId(settlementState.getSettlementEntity().getAccountId())
                .fromSettlementId(settlementState.getSettlementEntity().getId())
                .toAccountId(contractEntity.getToAccountId())
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
