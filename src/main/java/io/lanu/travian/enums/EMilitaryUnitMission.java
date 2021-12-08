package io.lanu.travian.enums;

public enum EMilitaryUnitMission {
    HOME("Own army"),
    CAUGHT("Caught"),
    ATTACK("Attack"),
    RAID("Raid"),
    REINFORCEMENT("Reinforcement");

    private String name;

    EMilitaryUnitMission(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
