package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.ShortVillageInfo;
import io.lanu.travian.game.models.responses.VillageView;

import java.util.List;

public interface VillageService {
    VillageEntity createVillage(NewVillageRequest newVillageRequest);
    VillageView getVillageById(String villageId);
    List<ShortVillageInfo> getAllVillagesByUserId(String userId);
}
