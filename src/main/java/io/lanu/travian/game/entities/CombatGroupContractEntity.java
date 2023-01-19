package io.lanu.travian.game.entities;

import io.lanu.travian.enums.ECombatGroupMission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombatGroupContractEntity {
    @Id
    private String id;
    private String ownerSettlementId;
    private String toAccountId;
    private ECombatGroupMission mission;
    private String targetVillageId;
    private List<Integer> units;
    private LocalDateTime arrivalTime;
    private int duration;
}
