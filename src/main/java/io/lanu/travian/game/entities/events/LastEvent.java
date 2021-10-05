package io.lanu.travian.game.entities.events;

import io.lanu.travian.game.entities.VillageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LastEvent implements IEvent {

    private LocalDateTime executionTime;

    @Override
    public void execute(VillageEntity villageEntity) {

    }
}
