package io.lanu.travian.game.services;

import io.lanu.travian.Consts;
import io.lanu.travian.enums.*;
import io.lanu.travian.game.dto.SettlementStateDTO;
import io.lanu.travian.game.entities.*;
import io.lanu.travian.game.models.requests.CombatGroupSendingRequest;
import io.lanu.travian.game.models.requests.OrderCombatUnitRequest;
import io.lanu.travian.game.models.responses.*;
import io.lanu.travian.game.repositories.CombatGroupContractRepository;
import io.lanu.travian.game.repositories.CombatGroupRepository;
import io.lanu.travian.game.repositories.ResearchedCombatUnitRepository;
import io.lanu.travian.templates.military.CombatUnitFactory;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
                .map(shortUnit -> CombatUnitFactory.getUnit(nation, shortUnit.getName(), shortUnit.getLevel()))
                .collect(Collectors.toList());
    }

    @Override
    public SettlementStateDTO orderCombatUnits(OrderCombatUnitRequest orderCombatUnitRequest, String settlementId) {
        var currentState = engineService.updateParticularSettlementState(settlementId, LocalDateTime.now());
        ECombatUnit unit = orderCombatUnitRequest.getUnitType();
        ModelMapper mapper = new ModelMapper();
        CombatUnitResponse mappedUnit = mapper.map(unit, CombatUnitResponse.class);
        mappedUnit.setSpeed(mappedUnit.getSpeed() / Consts.SPEED);
        mappedUnit.setTime(mappedUnit.getTime() / Consts.SPEED);

        List<OrderCombatUnitEntity> ordersList = currentState.getSettlementEntity().getCombatUnitOrders()
                .stream()
                .sorted(Comparator.comparing(OrderCombatUnitEntity::getCreated))
                .collect(Collectors.toList());

        LocalDateTime lastTime = ordersList.size() > 0 ? ordersList.get(ordersList.size() - 1).getEndOrderTime() : LocalDateTime.now();

        LocalDateTime endOrderTime = lastTime.plus(
                orderCombatUnitRequest.getAmount() * mappedUnit.getTime(), ChronoUnit.SECONDS);

        OrderCombatUnitEntity armyOrder = new OrderCombatUnitEntity(orderCombatUnitRequest.getVillageId(), lastTime,
                orderCombatUnitRequest.getUnitType(), orderCombatUnitRequest.getAmount(), mappedUnit.getTime(), mappedUnit.getEat(),
                endOrderTime);

        spendResources(orderCombatUnitRequest.getAmount(), currentState.getSettlementEntity(), mappedUnit);

        armyOrder.setCreated(LocalDateTime.now());
        ordersList.add(armyOrder);
        currentState.getSettlementEntity().setCombatUnitOrders(ordersList);

        return engineService.saveSettlementEntity(currentState);
    }

    private void spendResources(int unitsAmount, SettlementEntity settlementEntity, CombatUnitResponse kind) {
        var neededResources = kind.getCost().stream()
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
