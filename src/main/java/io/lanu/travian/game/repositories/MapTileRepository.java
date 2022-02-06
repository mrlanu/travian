package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.MapTile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MapTileRepository extends MongoRepository<MapTile, String> {
    List<MapTile> getAllByCorXBetweenAndCorYBetween(int fromX, int toX, int fromY, int toY);
    MapTile getByCorXAndCorY(int x, int y);
}
