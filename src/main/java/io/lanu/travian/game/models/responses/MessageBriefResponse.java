package io.lanu.travian.game.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageBriefResponse {
    private String id;
    private String subject;
    private String senderName;
    private String senderId;
    private String recipientName;
    private String recipientId;
    private boolean read;
    private LocalDateTime time;
}
