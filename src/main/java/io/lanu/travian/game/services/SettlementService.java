package io.lanu.travian.game.services;

import io.lanu.travian.game.dto.SettlementStateDTO;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.SettlementView;
import io.lanu.travian.game.models.responses.ShortVillageInfo;
import io.lanu.travian.game.models.responses.TileDetail;

import java.util.List;
import java.util.Optional;

public interface SettlementService {
    SettlementStateDTO saveSettlement(SettlementStateDTO settlementState);

    Optional<SettlementEntity> findById(String villageId);

    SettlementStateDTO newVillage(NewVillageRequest newVillageRequest);

    List<ShortVillageInfo> getAllVillagesByUserId(String userId);

    SettlementStateDTO updateName(String villageId, String newName);

    Optional<SettlementEntity> findVillageByCoordinates(int x, int y);

    TileDetail getTileDetail(SettlementEntity settlement, int fromX, int fromY);

    SettlementView getSettlementById(String settlementId);
    SettlementView getSettlementById(SettlementStateDTO currentState);
}