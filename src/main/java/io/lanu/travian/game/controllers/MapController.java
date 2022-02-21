package io.lanu.travian.game.controllers;

import io.lanu.travian.game.entities.MapTile;
import io.lanu.travian.game.models.requests.MapPart;
import io.lanu.travian.game.models.responses.TileDetail;
import io.lanu.travian.game.repositories.MapTileRepository;
import io.lanu.travian.game.services.SettlementState;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/world")
public class MapController {

    private final SettlementState state;
    private final MapTileRepository repository;

    public MapController(SettlementState state, MapTileRepository repository) {
        this.state = state;
        this.repository = repository;
    }

    @GetMapping("/tile-detail/{id}/{fromX}/{fromY}")
    public TileDetail getTileDetail(@PathVariable String id,
                                    @PathVariable int fromX,
                                    @PathVariable int fromY){
        return state.getTileDetail(id, fromX, fromY);
    }

    @PostMapping("/map-part")
    public List<MapTile> getPartOfMap(@RequestBody MapPart mapPart){
        return repository.getAllByCorXBetweenAndCorYBetween(
                mapPart.getFromX(), mapPart.getToX(),
                mapPart.getFromY(), mapPart.getToY());
    }

}
