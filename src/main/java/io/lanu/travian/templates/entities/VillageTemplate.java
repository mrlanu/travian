package io.lanu.travian.templates.entities;


import io.lanu.travian.game.models.Field;
import io.lanu.travian.enums.VillageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

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

    public VillageTemplate(VillageType villageType, List<Field> fields, Map<Integer, String> buildings) {
        this.villageType = villageType;
        this.fields = fields;
        this.buildings = buildings;
    }
}

