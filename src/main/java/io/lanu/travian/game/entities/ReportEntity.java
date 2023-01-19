package io.lanu.travian.game.entities;

import io.lanu.travian.enums.ECombatGroupMission;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("reports")
@Data
public class ReportEntity {
    @Id
    private String id;
    //since we create two reports (one for from, and one for to )
    private String reportOwner;
    private ECombatGroupMission mission;
    private ReportPlayerEntity off;
    private List<ReportPlayerEntity> def;
    private LocalDateTime dateTime;
    private boolean read;

    public ReportEntity(String reportOwner, ECombatGroupMission mission, ReportPlayerEntity off, List<ReportPlayerEntity> def,
                        LocalDateTime dateTime) {
        this.reportOwner = reportOwner;
        this.mission = mission;
        this.off = off;
        this.def = def;
        this.dateTime = dateTime;
    }
}
