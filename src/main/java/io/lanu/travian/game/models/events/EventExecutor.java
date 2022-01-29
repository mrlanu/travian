package io.lanu.travian.game.models.events;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventExecutor {
    private LocalDateTime executionTime;
    private EventStrategy strategy;

    public EventExecutor(EventStrategy strategy) {
        this.strategy = strategy;
    }

    public EventExecutor() {

    }

    public void tryExecute(){
        strategy.execute();
    }

    public LocalDateTime getExecutionTime(){
        return strategy.getExecutionTime();
    }
}
