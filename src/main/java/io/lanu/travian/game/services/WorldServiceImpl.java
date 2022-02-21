package io.lanu.travian.game.services;

import io.lanu.travian.enums.SettlementSubType;
import io.lanu.travian.enums.SettlementType;
import io.lanu.travian.game.entities.MapTile;
import io.lanu.travian.game.repositories.MapTileRepository;
import io.lanu.travian.templates.villages.VillageEntityFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorldServiceImpl implements WorldService{

    private final int WORLD_X = 50;
    private final int WORLD_Y = 50;
    private final int OASES_AMOUNT = 50;
    private final MapTileRepository repo;
    private final SettlementService settlementService;

    public WorldServiceImpl(MapTileRepository repo, SettlementService settlementService) {
        this.repo = repo;
        this.settlementService = settlementService;
    }

    @Override
    public void createWorld() {
        if (repo.count() == 0){
            createNewWorld(WORLD_X, WORLD_Y);
        }
    }

    private void createNewWorld(int xLength, int yLength) {
        var world = createBlueprint(xLength, yLength);
        insertOases(world, SettlementSubType.OASIS_CROP);
        insertOases(world, SettlementSubType.OASIS_WOOD);
        insertOases(world, SettlementSubType.OASIS_CROP);
        insertOases(world, SettlementSubType.OASIS_CLAY);
        repo.saveAll(world);
    }

    private List<MapTile> createBlueprint(int xLength, int yLength) {
        List<MapTile> world = new ArrayList<>();
        for (int y = 1; y < yLength + 1; y++){
            for (int x = 1; x < xLength + 1; x++){
                world.add(new MapTile(null, x, y, null, "Grass land", "grassland", true));
            }
        }
        return world;
    }

    private void insertOases(List<MapTile> world, SettlementSubType subType){
        for (int i = 0; i < OASES_AMOUNT; i++){
            var emptySpots = world.stream()
                    .filter(MapTile::isEmpty)
                    .collect(Collectors.toList());
            var emptySpot = emptySpots.get(getRandomNumber(0, emptySpots.size()));
            var entity = VillageEntityFactory.getVillageByType(SettlementType.OASIS, subType);
            entity.setX(emptySpot.getCorX());
            entity.setY(emptySpot.getCorY());
            entity.setName(subType.toString());
            var id = settlementService.saveVillage(entity).getId();
            emptySpot.setId(id);
            emptySpot.setName(subType.toString());
            emptySpot.setClazz(subType.toString().toLowerCase());
            emptySpot.setEmpty(false);
        }
    }

    private int getRandomNumber(int min, int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }

}
