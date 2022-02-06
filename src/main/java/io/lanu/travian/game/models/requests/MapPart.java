package io.lanu.travian.game.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MapPart {
    private int fromX;
    private int toX;
    private int fromY;
    private int toY;

    public void checkLimitsOfWorld(){
        if (fromX < -1){
            fromX = -1;
        }
        if (toX > 201){
            toX = 201;
        }
        if (fromY < -1){
            fromY = -1;
        }
        if (toY > 201){
            toY = 201;
        }
    }
}
