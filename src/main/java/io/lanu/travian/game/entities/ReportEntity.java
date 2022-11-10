package io.lanu.travian.game.entities;

import io.lanu.travian.enums.EMilitaryUnitMission;
import io.lanu.travian.game.models.ReportPlayer;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("reports")
@Data
public class ReportEntity {

    public ReportEntity(EMilitaryUnitMission mission, ReportPlayer attacker, ReportPlayer defender, LocalDateTime dateTime) {
        this.mission = mission;
        this.attacker = attacker;
        this.defender = defender;
        this.dateTime = dateTime;
    }

    @Id
    private String id;
    private EMilitaryUnitMission mission;
    private ReportPlayer attacker;
    private ReportPlayer defender;
    private LocalDateTime dateTime;
    private boolean read;
}
