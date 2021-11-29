package io.lanu.travian.game.entities;

import io.lanu.travian.enums.ECombatUnit;
import io.lanu.travian.enums.ENation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("military")
public class MilitaryUnitEntity {
    @Id
    private String id;
    private boolean homeMilitaryUnit;
    private ENation nation;
    private boolean dynamic;
    private String originVillageId;
    private String currentLocationVillageId;
    private Map<ECombatUnit, Integer> units;
}
