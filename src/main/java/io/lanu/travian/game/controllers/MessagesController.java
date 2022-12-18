package io.lanu.travian.game.controllers;

import io.lanu.travian.game.entities.MessageEntity;
import io.lanu.travian.game.models.requests.MessageSendRequest;
import io.lanu.travian.game.models.responses.MessageBriefResponse;
import io.lanu.travian.game.models.responses.MessageResponse;
import io.lanu.travian.game.services.MessagesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessagesController {
    private final MessagesService messagesService;

    public MessagesController(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @PostMapping()
    public MessageEntity addMessage(@RequestBody MessageSendRequest messageSendRequest){
        return messagesService.save(messageSendRequest);
    }

    @GetMapping()
    public List<MessageBriefResponse> getAll(@RequestParam String clientId, @RequestParam boolean sent){
        return messagesService.getAllBriefs(clientId, sent);
    }

    @GetMapping("/{messageId}")
    public MessageResponse getMessageById(@PathVariable String messageId){
        return messagesService.getMessageById(messageId);
    }

    @PutMapping("/delete")
    public void deleteMessage(@RequestBody List<String> messagesId, @RequestParam String requestedOwnerId){
        messagesService.delete(messagesId, requestedOwnerId);
    }
}
