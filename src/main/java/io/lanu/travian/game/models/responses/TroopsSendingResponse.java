package io.lanu.travian.game.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TroopsSendingResponse {
    private String attackingVillageId;
    private String targetName;
    private int targetX;
    private int targetY;
    private String targetPlayerName;
    private int[] troops;
    private long duration;
    private LocalDateTime arriveTime;
}
