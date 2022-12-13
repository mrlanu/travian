package io.lanu.travian.game.entities;

import io.lanu.travian.enums.ECombatGroupMission;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("reports")
@Data
public class ReportEntity {
    @Id
    private String id;
    //since we create two reports (one for from, and one for to )
    private String reportOwner;
    private ECombatGroupMission mission;
    private ReportPlayerEntity from;
    private ReportPlayerEntity to;
    private LocalDateTime dateTime;
    private boolean read;

    public ReportEntity(String reportOwner, ECombatGroupMission mission, ReportPlayerEntity from, ReportPlayerEntity to,
                        LocalDateTime dateTime) {
        this.reportOwner = reportOwner;
        this.mission = mission;
        this.from = from;
        this.to = to;
        this.dateTime = dateTime;
    }
}
