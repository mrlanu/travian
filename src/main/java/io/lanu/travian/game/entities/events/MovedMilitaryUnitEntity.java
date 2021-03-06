package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.ENation;
import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.models.responses.VillageBrief;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document("move-military-units")
@NoArgsConstructor
public class MovedMilitaryUnitEntity {
    @Id
    private String id;
    private ENation nation;
    private String mission;
    private int[] units;
    private Map<EResource, BigDecimal> plunder;

    private String originVillageId;
    private VillageBrief origin;

    private String targetVillageId;
    private VillageBrief target;

    private LocalDateTime executionTime;
    private int duration;
    private int eatExpenses;

    public MovedMilitaryUnitEntity(ENation nation, String mission, int[] units, Map<EResource, BigDecimal> plunder, String originVillageId,
                                   VillageBrief origin, String targetVillageId, VillageBrief target, LocalDateTime executionTime,
                                   int duration, int eatExpenses) {
        this.nation = nation;
        this.mission = mission;
        this.units = units;
        this.plunder = plunder;
        this.originVillageId = originVillageId;
        this.origin = origin;
        this.targetVillageId = targetVillageId;
        this.target = target;
        this.executionTime = executionTime;
        this.duration = duration;
        this.eatExpenses = eatExpenses;
    }
}
