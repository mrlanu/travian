package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.events.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findAllByVillageId(String villageId);
    void deleteByEventId(String eventId);
}
