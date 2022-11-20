package io.lanu.travian.game.entities;

import io.lanu.travian.enums.ECombatUnitMission;
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
    private ENation nation;
    private String ownerUserName;
    private String ownerAccountId;
    private String ownerSettlementId;
    private String ownerSettlementName;
    private String toSettlementId;
    private LocalDateTime executionTime;
    private int duration;
    private ECombatUnitMission mission;
    private int[] units;
    private Map<EResource, BigDecimal> plunder;
}