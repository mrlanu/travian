package io.lanu.travian.game.services;

import io.lanu.travian.enums.EOasesKind;
import io.lanu.travian.enums.EVillageType;
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
    private final VillageService villageService;

    public WorldServiceImpl(MapTileRepository repo, VillageService villageService) {
        this.repo = repo;
        this.villageService = villageService;
    }

    @Override
    public void createWorld() {
        if (repo.count() == 0){
            createNewWorld(WORLD_X, WORLD_Y);
        }
    }

    private void createNewWorld(int xLength, int yLength) {
        var world = createBlueprint(xLength, yLength);
        insertOases(world, EOasesKind.CROP);
        insertOases(world, EOasesKind.WOOD);
        insertOases(world, EOasesKind.IRON);
        insertOases(world, EOasesKind.CLAY);
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

    private void insertOases(List<MapTile> world, EOasesKind kind){
        for (int i = 0; i < OASES_AMOUNT; i++){
            var emptySpots = world.stream()
                    .filter(MapTile::isEmpty)
                    .collect(Collectors.toList());
            var emptySpot = emptySpots.get(getRandomNumber(0, emptySpots.size()));
            var entity = VillageEntityFactory.getOasis(kind);
            entity.setX(emptySpot.getCorX());
            entity.setY(emptySpot.getCorY());
            entity.setName(kind.getName());
            emptySpot.setName(kind.getName());
            emptySpot.setClazz(kind.getClazz());
            emptySpot.setEmpty(false);
            villageService.saveVillage(entity);
        }
    }

    private int getRandomNumber(int min, int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }

}
