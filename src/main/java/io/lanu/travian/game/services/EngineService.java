package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.repositories.CombatGroupRepository;
import io.lanu.travian.game.repositories.ReportRepository;
import io.lanu.travian.game.repositories.SettlementRepository;

import java.time.LocalDateTime;

public interface EngineService {
    SettlementEntity recalculateCurrentState(String villageId, LocalDateTime untilTime);
    SettlementEntity save(SettlementEntity settlement);
    CombatGroupRepository getCombatGroupRepository();
    ReportRepository getReportRepository();
    SettlementRepository getSettlementRepository();
}
