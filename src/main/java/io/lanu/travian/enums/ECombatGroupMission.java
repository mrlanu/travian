package io.lanu.travian.enums;

//@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ECombatGroupMission {
    HOME("Own army"),
    BACK("Return to home"),
    CAUGHT("Caught"),
    ATTACK("Attack"),
    RAID("Raid"),
    REINFORCEMENT("Reinforcement");

    private final String name;

    ECombatGroupMission(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
