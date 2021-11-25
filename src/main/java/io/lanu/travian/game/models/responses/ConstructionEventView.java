package io.lanu.travian.game.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConstructionEventView {
    private String id;
    private int position;
    private String name;
    private int toLevel;
    private LocalDateTime completeTime;
    private long timeLeft;
}
