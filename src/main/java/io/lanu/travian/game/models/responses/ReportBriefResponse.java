package io.lanu.travian.game.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportBriefResponse {
    private String id;
    private boolean read;
    private String subject;
    private LocalDateTime received;
}
