package io.lanu.travian.game.controllers;

import io.lanu.travian.game.entities.MapTile;
import io.lanu.travian.game.models.requests.MapPart;
import io.lanu.travian.game.models.responses.TileDetail;
import io.lanu.travian.game.repositories.MapTileRepository;
import io.lanu.travian.game.services.IState;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/world")
public class MapController {

    private final IState state;
    private final MapTileRepository repository;

    public MapController(IState state, MapTileRepository repository) {
        this.state = state;
        this.repository = repository;
    }

    @GetMapping("/tile-detail/{id}")
    public TileDetail getTileDetail(@PathVariable String id){
        return state.getTileDetail(id);
    }

    @PostMapping("/map-part")
    public List<MapTile> getPartOfMap(@RequestBody MapPart mapPart){
        return repository.getAllByCorXBetweenAndCorYBetween(
                mapPart.getFromX(), mapPart.getToX(),
                mapPart.getFromY(), mapPart.getToY());
    }

}
