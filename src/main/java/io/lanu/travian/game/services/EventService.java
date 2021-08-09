package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.events.Event;
import io.lanu.travian.game.models.requests.FieldUpgradeRequest;

import java.time.LocalDateTime;

public interface EventService {
    Event createFieldUpgradeEvent(FieldUpgradeRequest fieldUpgradeRequest);
}
