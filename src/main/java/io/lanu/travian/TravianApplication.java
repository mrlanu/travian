package io.lanu.travian;

import io.lanu.travian.game.services.WorldService;
import io.lanu.travian.security.AuthRequest;
import io.lanu.travian.security.UsersService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableMongoAuditing
public class TravianApplication {

    private final WorldService worldService;
    //just for fake users, should be removed

    public TravianApplication(WorldService worldService) {
        this.worldService = worldService;
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
        return args -> worldService.createWorld();
    }
}
