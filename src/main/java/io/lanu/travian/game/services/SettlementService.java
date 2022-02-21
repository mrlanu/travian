package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.ShortVillageInfo;

import java.util.List;
import java.util.Optional;

public interface SettlementService {
    SettlementEntity saveVillage(SettlementEntity settlementEntity);
    SettlementEntity findById(String villageId);
    SettlementEntity newVillage(NewVillageRequest newVillageRequest);
    List<ShortVillageInfo> getAllVillagesByUserId(String userId);
    SettlementEntity updateName(SettlementEntity settlementEntity, String newName);
    Optional<SettlementEntity> findVillageByCoordinates(int x, int y);
}
