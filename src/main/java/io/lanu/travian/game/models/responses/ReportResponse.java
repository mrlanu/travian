package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.ECombatGroupMission;
import io.lanu.travian.game.models.ReportPlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private String id;
    //since we create two reports (one for from, and one for to )
    private String reportOwner;
    private ECombatGroupMission mission;
    private ReportPlayer from;
    private ReportPlayer to;
    private LocalDateTime dateTime;
    private boolean read;
}
