package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.events.Event;

public interface EventService {

    Event createFieldUpgradeEvent(String villageId, Integer fieldPosition);
}
