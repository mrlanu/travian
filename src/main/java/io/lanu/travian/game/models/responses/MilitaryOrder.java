package io.lanu.travian.game.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilitaryOrder {
    private String unit;
    private int amount;
    private long duration;
    private long eachDuration;
    private LocalDateTime endOrder;
}
