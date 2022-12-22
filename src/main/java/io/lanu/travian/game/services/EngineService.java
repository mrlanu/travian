package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.repositories.CombatGroupRepository;
import io.lanu.travian.game.repositories.ReportRepository;
import io.lanu.travian.game.repositories.SettlementRepository;

public interface EngineService {
    SettlementEntity recalculateCurrentState(String villageId);
    SettlementEntity save(SettlementEntity settlement);
    CombatGroupRepository getCombatGroupRepository();
    ReportRepository getReportRepository();
    SettlementRepository getSettlementRepository();
}
