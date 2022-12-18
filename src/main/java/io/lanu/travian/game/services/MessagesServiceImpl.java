package io.lanu.travian.game.services;

import io.lanu.travian.game.entities.MessageEntity;
import io.lanu.travian.game.models.requests.MessageSendRequest;
import io.lanu.travian.game.models.responses.MessageBriefResponse;
import io.lanu.travian.game.models.responses.MessageResponse;
import io.lanu.travian.game.repositories.MessagesRepository;
import io.lanu.travian.security.UsersRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessagesServiceImpl implements MessagesService{

    private final MessagesRepository messagesRepository;
    private final UsersRepository usersRepository;

    public MessagesServiceImpl(MessagesRepository messagesRepository, UsersRepository usersRepository) {
        this.messagesRepository = messagesRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public MessageResponse getMessageById(String messageId){
        var mapper = new ModelMapper();
        var entity = messagesRepository.findById(messageId).orElseThrow();
        return mapper.map(entity, MessageResponse.class);
    }

    @Override
    public long countNewMessages(String recipientId) {
        return messagesRepository.countAllByRecipientIdAndVisibleForRecipientAndRead(recipientId, true, false);
    }

    @Override
    public List<MessageBriefResponse> getAllBriefs(String clientId, boolean sent) {
        List<MessageEntity> allMessages;
        if (sent){
            allMessages = messagesRepository
                    .findAllBySenderIdAndVisibleForSender(clientId, true);
        } else {
            allMessages = messagesRepository
                    .findAllByRecipientIdAndVisibleForRecipient(clientId, true);
        }
        return allMessages.stream()
                .map(messageEntity ->
                        new MessageBriefResponse(
                                messageEntity.getId(), messageEntity.getSubject(),
                                messageEntity.getSenderName(), messageEntity.getSenderId(),
                                messageEntity.getRecipientName(), messageEntity.getRecipientId(),
                                messageEntity.isRead(), messageEntity.getDateTime()))
                .collect(Collectors.toList());
    }

    @Override
    public MessageEntity save(MessageSendRequest messageSendRequest) {
        var recipient = usersRepository.findByUsername(messageSendRequest.getRecipientName())
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("User with username - %s is not exist.",
                                messageSendRequest.getRecipientName())));
        var messageEntity = new MessageEntity(null, messageSendRequest.getSubject(), messageSendRequest.getBody(),
                messageSendRequest.getSenderId(), messageSendRequest.getSenderName(), true,
                true, recipient.getUserId(),
                messageSendRequest.getRecipientName(), LocalDateTime.now(), false);
        return messagesRepository.save(messageEntity);
    }

    @Override
    public void read(List<String> messagesId) {
        messagesRepository.findAllById(messagesId).forEach(
                messageEntity -> {
                    messageEntity.setRead(true);
                    messagesRepository.save(messageEntity);
                }
        );
    }

    @Override
    public boolean delete(List<String> messagesId, String requestOwnerId){
        messagesRepository.findAllById(messagesId).forEach(
                message -> {
                    if (requestOwnerId.equals(message.getSenderId())){
                        if (message.isVisibleForRecipient()){
                            message.setVisibleForSender(false);
                            messagesRepository.save(message);
                        }else {
                            messagesRepository.deleteById(message.getId());
                        }
                    }else {
                        if (message.isVisibleForSender()){
                            message.setVisibleForRecipient(false);
                            messagesRepository.save(message);
                        }else {
                            messagesRepository.deleteById(message.getId());
                        }
                    }
                }
        );
        return true;
    }
}
