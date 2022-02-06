package io.lanu.travian.game.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("world")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MapTile {
    @Id
    private String id;
    private int corX;
    private int corY;
    private String name;
    private String clazz;
    private String color;
}
