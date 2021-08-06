package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.requests.NewVillageRequest;

public interface VillageService {
    VillageEntity createVillage(NewVillageRequest newVillageRequest);
}
