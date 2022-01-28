package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.VillageEntity;

public interface IState {
    VillageEntity getState(String villageId);
    VillageEntity saveState(VillageEntity village);
}
