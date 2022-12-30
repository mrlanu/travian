package io.lanu.travian.game.controllers;

import io.lanu.travian.game.entities.MapTile;
import io.lanu.travian.game.models.requests.MapPart;
import io.lanu.travian.game.models.responses.TileDetail;
import io.lanu.travian.game.repositories.MapTileRepository;
import io.lanu.travian.game.services.SettlementService;
import io.lanu.travian.game.services.EngineService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/world")
public class MapController {

    private final EngineService state;
    private final MapTileRepository repository;

    private final SettlementService settlementService;

    public MapController(EngineService state, MapTileRepository repository, SettlementService settlementService) {
        this.state = state;
        this.repository = repository;
        this.settlementService = settlementService;
    }

    @GetMapping("/tile-detail/{id}/{fromX}/{fromY}")
    public TileDetail getTileDetail(@PathVariable String id,
                                    @PathVariable int fromX,
                                    @PathVariable int fromY){
        var settlementState = state.recalculateCurrentState(id, LocalDateTime.now());
        return settlementService.getTileDetail(settlementState, fromX, fromY);
    }

    @PostMapping("/map-part")
    public List<MapTile> getPartOfMap(@RequestBody MapPart mapPart){
        return repository.getAllByCorXBetweenAndCorYBetween(
                mapPart.getFromX(), mapPart.getToX(),
                mapPart.getFromY(), mapPart.getToY());
    }

}
