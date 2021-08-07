package io.lanu.travian.templates;

import io.lanu.travian.enums.Resource;
import io.lanu.travian.enums.VillageType;
import io.lanu.travian.game.models.Field;
import io.lanu.travian.templates.entities.VillageTemplate;
import io.lanu.travian.templates.repositories.VillageTemplatesRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
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
                                new Field(0, Resource.CROP, 0, BigDecimal.TEN,
                                        Map.of(
                                                Resource.CROP, BigDecimal.TEN,
                                                Resource.CLAY, BigDecimal.TEN,
                                                Resource.IRON, BigDecimal.TEN,
                                                Resource.TIME, BigDecimal.TEN),
                                        false, false),
                                new Field(1, Resource.CROP, 0, BigDecimal.TEN,
                                        Map.of(
                                                Resource.CROP, BigDecimal.TEN,
                                                Resource.CLAY, BigDecimal.TEN,
                                                Resource.IRON, BigDecimal.TEN,
                                                Resource.TIME, BigDecimal.TEN),
                                        false, false),
                                new Field(2, Resource.CLAY, 0, BigDecimal.TEN,
                                        Map.of(
                                                Resource.CROP, BigDecimal.TEN,
                                                Resource.CLAY, BigDecimal.TEN,
                                                Resource.IRON, BigDecimal.TEN,
                                                Resource.TIME, BigDecimal.TEN),
                                        false, false),
                                new Field(2, Resource.WOOD, 0, BigDecimal.TEN,
                                        Map.of(
                                                Resource.CROP, BigDecimal.TEN,
                                                Resource.CLAY, BigDecimal.TEN,
                                                Resource.IRON, BigDecimal.TEN,
                                                Resource.TIME, BigDecimal.TEN),
                                        false, false),
                                new Field(2, Resource.IRON, 0, BigDecimal.TEN,
                                        Map.of(
                                                Resource.CROP, BigDecimal.TEN,
                                                Resource.CLAY, BigDecimal.TEN,
                                                Resource.IRON, BigDecimal.TEN,
                                                Resource.TIME, BigDecimal.TEN),
                                        false, false)
                        ),
                        Collections.emptyMap(), Map.of(
                                Resource.CROP, BigDecimal.valueOf(750),
                                Resource.CLAY, BigDecimal.valueOf(750),
                                Resource.IRON, BigDecimal.valueOf(750),
                                Resource.WOOD, BigDecimal.valueOf(750)
                        ));

                villageTemplatesRepo.save(village);
            }
        };
    }

}
