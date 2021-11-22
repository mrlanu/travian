package io.lanu.travian.game.entities;

import io.lanu.travian.game.models.ResearchedTroopShort;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("researched-troops")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResearchedTroopsEntity {
    private String villageId;
    private List<ResearchedTroopShort> units;
}
