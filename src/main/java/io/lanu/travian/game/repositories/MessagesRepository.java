package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.MessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessagesRepository extends MongoRepository<MessageEntity, String> {
    List<MessageEntity> findAllByRecipientIdAndVisibleForRecipient(String recipientId, boolean visible);
    List<MessageEntity> findAllBySenderIdAndVisibleForRecipient(String senderId, boolean visible);
    List<MessageEntity> findAllBySenderIdAndVisibleForSender(String senderId, boolean visible);
}
