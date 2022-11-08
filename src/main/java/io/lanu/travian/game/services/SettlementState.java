package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.repositories.MilitaryUnitRepository;
import io.lanu.travian.game.repositories.MovedMilitaryUnitRepository;

public interface SettlementState {
    SettlementEntity recalculateCurrentState(String villageId);
    SettlementEntity save(SettlementEntity settlement);
    MovedMilitaryUnitRepository getMovedMilitaryUnitRepository();
    MilitaryUnitRepository getMilitaryUnitRepository();
}
