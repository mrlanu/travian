package io.lanu.travian.game.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventView {
    private String event;
    private LocalDateTime completeTime;
    private String timeLeft;
}
