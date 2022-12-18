package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.MessageEntity;
import io.lanu.travian.game.models.requests.MessageSendRequest;
import io.lanu.travian.game.models.responses.MessageBriefResponse;
import io.lanu.travian.game.models.responses.MessageResponse;

import java.util.List;

public interface MessagesService {
    List<MessageBriefResponse> getAllBriefs(String clientId, boolean sent);

    MessageEntity save(MessageSendRequest messageSendRequest);
    void read(List<String> messagesId);
    boolean delete(List<String> messagesId, String requestOwnerId);
    MessageResponse getMessageById(String messageId);
}
