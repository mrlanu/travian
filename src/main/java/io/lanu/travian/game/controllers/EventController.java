package io.lanu.travian.game.controllers;

import io.lanu.travian.game.entities.events.Event;
import io.lanu.travian.game.entities.events.FieldUpgradeEvent;
import io.lanu.travian.game.entities.events.TroopEvent;
import io.lanu.travian.game.models.requests.FieldUpgradeRequest;
import io.lanu.travian.game.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/upgrade-field")
    public ResponseEntity<String> newVillage(@RequestBody FieldUpgradeRequest fieldUpgradeRequest){
        Event event = eventService.createFieldUpgradeEvent(fieldUpgradeRequest);
        return ResponseEntity.status(HttpStatus.OK).body("New Event ID : " + event.getEventId());
    }

    /*@PostMapping("/troop")
    public ResponseEntity<String> newTroop(*//*@RequestBody NewVillageRequest newVillageRequest*//*){
        TroopEvent event = new TroopEvent(LocalDateTime.now(), 30);
        event = eventRepository.save(event);
        return ResponseEntity.status(HttpStatus.OK).body("New Event ID : " + event.getEventId());
    }*/
}
