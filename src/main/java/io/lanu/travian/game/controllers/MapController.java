package io.lanu.travian.game.controllers;

import io.lanu.travian.game.entities.MapTile;
import io.lanu.travian.game.models.requests.MapPart;
import io.lanu.travian.game.repositories.MapTileRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/world")
public class MapController {

    private final MapTileRepository repository;

    public MapController(MapTileRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/create-map")
    public void createMap(){

    }

    @PostMapping("/map-part")
    public List<MapTile> getPartOfMap(@RequestBody MapPart mapPart){
        return repository.getAllByCorXBetweenAndCorYBetween(
                mapPart.getFromX(), mapPart.getToX(),
                mapPart.getFromY(), mapPart.getToY());
    }

}
