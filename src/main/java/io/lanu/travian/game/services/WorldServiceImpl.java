package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.MapTile;
import io.lanu.travian.game.repositories.MapTileRepository;
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

    public WorldServiceImpl(MapTileRepository repo) {
        this.repo = repo;
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
            emptySpot.setName(kind.name);
            emptySpot.setClazz(kind.clazz);
            emptySpot.setEmpty(false);
        }
    }

    private int getRandomNumber(int min, int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    private enum EOasesKind{
        WOOD("Wood oasis", "oasis-wood-free"),
        IRON("Iron oasis", "oasis-iron-free"),
        CLAY("Clay oasis", "oasis-clay-free"),
        CROP("Crop oasis", "oasis-crop-free");

        private final String name;
        private final String clazz;

        EOasesKind(String name, String clazz) {
            this.name = name;
            this.clazz = clazz;
        }
    }
}
