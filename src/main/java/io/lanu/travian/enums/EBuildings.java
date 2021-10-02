package io.lanu.travian.enums;

public enum EBuildings {
    WOODCUTTER("Woodcutter"),
    CLAY_PIT("Clay pit"),
    IRON_MINE("Iron mine"),
    CROPLAND("Cropland"),
    BARRACK("Barrack"),
    GRANARY("Granary"),
    MAIN("Main building"),
    WAREHOUSE("Warehouse"),
    EMPTY("Spot");

    private String name;

    EBuildings(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
