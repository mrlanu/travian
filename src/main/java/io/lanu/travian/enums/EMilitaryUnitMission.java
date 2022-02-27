package io.lanu.travian.enums;

public enum EMilitaryUnitMission {
    HOME("Own army"),
    BACK("Return to home"),
    CAUGHT("Caught"),
    ATTACK("Attack"),
    RAID("Raid"),
    REINFORCEMENT("Reinforcement");

    private final String name;

    EMilitaryUnitMission(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
