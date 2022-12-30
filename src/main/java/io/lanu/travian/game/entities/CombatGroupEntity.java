package io.lanu.travian.game.entities;

import io.lanu.travian.enums.ECombatGroupMission;
import io.lanu.travian.enums.ENation;
import io.lanu.travian.enums.EResource;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Document("combat-groups")
@Data
@Builder
public class CombatGroupEntity {
    @Id
    private String id;
    private boolean moved;
    private String ownerSettlementId;
    private ENation ownerNation;
    private String fromSettlementId;
    private String toSettlementId;
    private LocalDateTime executionTime;
    private int duration;
    private ECombatGroupMission mission;
    private int[] units;
    private Map<EResource, BigDecimal> plunder;
}