package io.lanu.travian.game.services;

import io.lanu.travian.game.dto.SettlementStateDTO;
import io.lanu.travian.game.repositories.CombatGroupRepository;
import io.lanu.travian.game.repositories.ReportRepository;
import io.lanu.travian.game.repositories.SettlementRepository;

import java.time.LocalDateTime;

public interface EngineService {
    void checkAllAccountEvents(String exceptSettlementId);
    SettlementStateDTO recalculateCurrentState(String villageId, LocalDateTime untilTime);
    SettlementStateDTO saveSettlementEntity(SettlementStateDTO currentState);
    CombatGroupRepository getCombatGroupRepository();
    ReportRepository getReportRepository();
    SettlementRepository getSettlementRepository();
}
