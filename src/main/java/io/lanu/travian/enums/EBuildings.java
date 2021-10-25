package io.lanu.travian.enums;

public enum EBuildings {
    WOODCUTTER("woodcutter"),
    CLAY_PIT("clay-pit"),
    IRON_MINE("iron-mine"),
    CROPLAND("cropland"),
    BARRACK("barrack"),
    GRANARY("granary"),
    MAIN("main-building"),
    WAREHOUSE("warehouse"),
    EMPTY("empty-spot");

    private String name;

    EBuildings(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
