package io.lanu.travian.templates.entities;


import io.lanu.travian.enums.Resource;
import io.lanu.travian.game.models.Field;
import io.lanu.travian.enums.VillageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Document("villages-templates")
@NoArgsConstructor
@AllArgsConstructor
public class VillageTemplate {

    private String villageId;
    private String accountId;
    private int x;
    private int y;
    private VillageType villageType;
    private int population;
    private int culture;
    private List<Field> fields;
    private Map<Integer, String> buildings;
    private Map<Resource, BigDecimal> storage;
    private Map<Resource, BigDecimal> producePerHour;

    public VillageTemplate(VillageType villageType, List<Field> fields, Map<Integer, String> buildings,
                           Map<Resource, BigDecimal> storage) {
        this.villageType = villageType;
        this.fields = fields;
        this.buildings = buildings;
        this.storage = storage;
        sumProducePerHour();
    }

    private void sumProducePerHour(){
         producePerHour = fields.stream()
                .collect(Collectors.groupingBy(Field::getFieldType,
                        Collectors.reducing(BigDecimal.ZERO, Field::getProduction, BigDecimal::add)));
    }
}

