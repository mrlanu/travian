package io.lanu.travian.templates;

import io.lanu.travian.enums.Resource;
import io.lanu.travian.enums.VillageType;
import io.lanu.travian.game.models.Field;
import io.lanu.travian.templates.entities.VillageTemplate;
import io.lanu.travian.templates.repositories.VillageTemplatesRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Configuration
public class TemplateConfig {

    private final VillageTemplatesRepo villageTemplatesRepo;

    public TemplateConfig(VillageTemplatesRepo villageTemplatesRepo) {
        this.villageTemplatesRepo = villageTemplatesRepo;
    }

    @Bean
    public CommandLineRunner createSomeVillages(){
        return args -> {
            if (villageTemplatesRepo.findAll().size() == 0){
                VillageTemplate village = new VillageTemplate(
                        VillageType.SIX,
                        Arrays.asList(
                                new Field(0, Resource.CROP, 0, 10,
                                        Map.of(
                                                Resource.CROP, 10,
                                                Resource.CLAY, 10,
                                                Resource.IRON, 10,
                                                Resource.TIME, 30),
                                        false, false),
                                new Field(1, Resource.CROP, 0, 10,
                                        Map.of(
                                                Resource.CROP, 10,
                                                Resource.CLAY, 10,
                                                Resource.IRON, 10,
                                                Resource.TIME, 30),
                                        false, false),
                                new Field(2, Resource.CLAY, 0, 10,
                                        Map.of(
                                                Resource.CROP, 10,
                                                Resource.CLAY, 10,
                                                Resource.IRON, 10,
                                                Resource.TIME, 30),
                                        false, false)
                        ),
                        Collections.emptyMap());

                villageTemplatesRepo.save(village);
            }
        };
    }

}
