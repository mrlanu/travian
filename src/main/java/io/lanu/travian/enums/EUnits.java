package io.lanu.travian.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EUnits {
    PHALANX("Phalanx", 15, 40, 50, 7, 35,
            Map.of(EResource.WOOD, 100, EResource.CLAY, 130, EResource.IRON, 55, EResource.CROP, 30),
            1, 60, "Phalanx is a cheap and fast in learning infant unit."), // time should be 1040
    LEGIONNAIRE("Legionnaire", 40, 35, 50, 6, 50,
            Map.of(EResource.WOOD, 120, EResource.CLAY, 100, EResource.IRON, 150, EResource.CROP, 30),
            1, 1600, "Legionnaire is a good infant unit.");
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

    EUnits(String name, int attack, int defInfantry, int defCavalry, int speed, int capacity,
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
