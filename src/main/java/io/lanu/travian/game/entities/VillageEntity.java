package io.lanu.travian.game.entities;

import io.lanu.travian.game.models.Field;
import io.lanu.travian.enums.VillageType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Document("villages")
@NoArgsConstructor
public class VillageEntity {
    @Id
    private String villageId;
    private String accountId;
    private int x;
    private int y;
    private VillageType villageType;
    private int population;
    private int culture;
    private List<Field> fields;
    private Map<Integer, String> buildings;
}
