package io.lanu.travian.game.models.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LastEventStrategy extends EventStrategy{

    private LocalDateTime executionTime;

    @Override
    public void execute() {

    }
}
