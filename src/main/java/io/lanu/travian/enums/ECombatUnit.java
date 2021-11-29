package io.lanu.travian.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ECombatUnit {
    HERO("Hero", 15, 40, 50, 7, 35,
            Map.of(EResource.WOOD, 100, EResource.CLAY, 130, EResource.IRON, 55, EResource.CROP, 30),
            1, 60, "Hero."),// time should be 1040
    PHALANX("Phalanx", 15, 40, 50, 7, 35,
            Map.of(EResource.WOOD, 100, EResource.CLAY, 130, EResource.IRON, 55, EResource.CROP, 30),
            1, 60, "Phalanx is a cheap and fast in learning infant unit."),// time should be 1040
    SWORDSMAN("Swordsman", 65, 35, 20, 6, 45,
            Map.of(EResource.WOOD, 140, EResource.CLAY, 150, EResource.IRON, 185, EResource.CROP, 60),
            1, 1440, "Swordsman description"),
    PATHFINDER("Pathfinder", 0, 20, 10, 17, 0,
            Map.of(EResource.WOOD, 170, EResource.CLAY, 150, EResource.IRON, 20, EResource.CROP, 40),
            2, 1320, "Pathfinder description"),
    THUNDER("Theutates thunder", 90, 25, 40, 19, 75,
            Map.of(EResource.WOOD, 350, EResource.CLAY, 450, EResource.IRON, 230, EResource.CROP, 60),
            2, 2480, "Thunder description"),
    DRUIDRIDER("Druidrider", 45, 115, 55, 16, 35,
            Map.of(EResource.WOOD, 360, EResource.CLAY, 330, EResource.IRON, 280, EResource.CROP, 120),
            2, 2560, "Druidrider description"),
    HAEDUAN("Haeduan", 140, 60, 165, 13, 65,
               Map.of(EResource.WOOD, 500, EResource.CLAY, 620, EResource.IRON, 675, EResource.CROP, 170),
            3, 3120, "Haeduan description"),
    RAM("Ram", 50, 30, 105, 4, 0,
            Map.of(EResource.WOOD, 950, EResource.CLAY, 555, EResource.IRON, 330, EResource.CROP, 75),
            3, 5000, "Ram description"),
    TREBUCHET("Trebuchet", 70, 45, 10, 3, 0,
            Map.of(EResource.WOOD, 960, EResource.CLAY, 1450, EResource.IRON, 630, EResource.CROP, 90),
            6, 9000, "Trebuchet description"),
    CHIEFTAIN("Chieftain", 40, 50, 50, 5, 0,
            Map.of(EResource.WOOD, 30750, EResource.CLAY, 45400, EResource.IRON, 31000, EResource.CROP, 37500),
            4, 90700, "Chieftain description"),
    SETTLER("Settler", 0, 80, 80, 5, 3000,
            Map.of(EResource.WOOD, 4400, EResource.CLAY, 5600, EResource.IRON, 4200, EResource.CROP, 3900),
            1, 22700, "Settler description");
    //PRAETORIAN;

    private final String name;

    private int level;
    private final int attack;
    private final int defInfantry;
    private final int defCavalry;
    private final int speed;
    private final int capacity;
    private final Map<EResource, Integer> cost;
    private final int eat;
    private long time;
    private final String description;

    ECombatUnit(String name, int attack, int defInfantry, int defCavalry, int speed, int capacity,
                Map<EResource, Integer> cost, int eat, int time, String description) {
        this.name = name;
        this.level = 0;
        this.attack = attack;
        this.defInfantry = defInfantry;
        this.defCavalry = defCavalry;
        this.speed = speed;
        this.capacity = capacity;
        this.cost = cost;
        this.eat = eat;
        this.time = time;
        this.description = description;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
