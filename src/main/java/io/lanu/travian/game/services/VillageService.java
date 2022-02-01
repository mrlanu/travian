package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.NewBuilding;
import io.lanu.travian.game.models.responses.ShortVillageInfo;
import io.lanu.travian.game.models.responses.VillageView;

import java.util.List;
import java.util.Optional;

public interface VillageService {
    VillageEntity saveVillage(VillageEntity villageEntity);
    VillageEntity findById(String villageId);
    VillageEntity newVillage(NewVillageRequest newVillageRequest);
    List<ShortVillageInfo> getAllVillagesByUserId(String userId);
    VillageEntity updateName(VillageEntity villageEntity, String newName);
    Optional<VillageEntity> findVillageByCoordinates(int x, int y);
}
