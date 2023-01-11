package io.lanu.travian.game.models.battle;

import io.lanu.travian.enums.ECombatGroupMission;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Side {
    private ESide side;
    private int population;
    private List<Unit> units;
    private List<Integer> numbers;
    private List<Integer> upgrades;
    private ECombatGroupMission mission;
    private List<Integer> targets;
    private boolean party;
    private boolean brew;

    public enum ESide{
        OFF, DEF
    }
}
