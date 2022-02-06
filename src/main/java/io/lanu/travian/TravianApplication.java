package io.lanu.travian;

import io.lanu.travian.game.entities.MapTile;
import io.lanu.travian.game.repositories.MapTileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableMongoAuditing
public class TravianApplication {

    private final MapTileRepository repository;

    public TravianApplication(MapTileRepository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {
        SpringApplication.run(TravianApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner isWorldExist(){
        return args -> {
            if (repository.count() == 0){
                createNewWorld(50, 50);
            }
        };
    }

    private void createNewWorld(int xLength, int yLength) {
        List<MapTile> world = new ArrayList<>();
        for (int y = 1; y < yLength + 1; y++){
            for (int x = 1; x < xLength + 1; x++){
                world.add(new MapTile(null, x, y, "Grass land", "grassland", "lightblue"));
            }
        }
        repository.saveAll(world);
    }
}
