package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.models.responses.VillageView;

public interface VillageService {
    VillageEntity createVillage(NewVillageRequest newVillageRequest);

    VillageView getVillageById(String villageId);
}
