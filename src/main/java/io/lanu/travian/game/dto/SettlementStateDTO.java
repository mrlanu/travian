package io.lanu.travian.game.dto;

import io.lanu.travian.enums.ECombatGroupLocation;
import io.lanu.travian.game.entities.CombatGroupEntity;
import io.lanu.travian.game.entities.SettlementEntity;
import io.lanu.travian.game.models.responses.CombatGroupView;
import io.lanu.travian.game.models.responses.TroopMovementsBrief;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementStateDTO {
    private SettlementEntity settlementEntity;
    private Map<String, TroopMovementsBrief> movementsBriefMap;
    private Map<ECombatGroupLocation, List<CombatGroupView>> combatGroupByLocationMap;
    @Builder.Default
    private List<CombatGroupEntity> combatGroupsInSettlement = new ArrayList<>();
}
