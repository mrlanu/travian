package io.lanu.travian.enums;

public enum EMilitaryUnitState {
    HOME("Armies in this village"),
    IN("Incoming armies"),
    OUT("Outgoing armies"),
    AWAY("Armies in other places");

    private String name;

    EMilitaryUnitState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
