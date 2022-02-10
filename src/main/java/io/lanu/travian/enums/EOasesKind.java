package io.lanu.travian.enums;

public enum EOasesKind {
    WOOD("Wood oasis", "oasis-wood-free"),
    IRON("Iron oasis", "oasis-iron-free"),
    CLAY("Clay oasis", "oasis-clay-free"),
    CROP("Crop oasis", "oasis-crop-free");

    private final String name;
    private final String clazz;

    EOasesKind(String name, String clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public String getClazz() {
        return clazz;
    }
}
