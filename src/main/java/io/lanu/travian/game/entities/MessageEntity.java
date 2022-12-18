package io.lanu.travian.game.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity {
    @Id
    private String id;
    private String subject;
    private String body;
    private String senderId;
    private String senderName;
    private boolean visibleForSender;
    private boolean visibleForRecipient;
    private String recipientId;
    private String recipientName;
    private LocalDateTime dateTime;
    private boolean read;
}
