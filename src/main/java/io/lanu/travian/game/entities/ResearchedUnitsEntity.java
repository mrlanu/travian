package io.lanu.travian.game.entities;

import io.lanu.travian.game.models.ResearchedUnitShort;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("researched-units")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResearchedUnitsEntity {
    private String villageId;
    private List<ResearchedUnitShort> units;
}
